# Requirements
#
# - Login with AWS: aws --profile cnb sso login

from dotenv import load_dotenv

import openchallenges_client
from pprint import pprint
from openchallenges_client.api import challenge_api

load_dotenv()

# List challenges from OC.io

# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_client.Configuration(
    host="https://openchallenges.io/api/v1"
)

# Enter a context with an instance of the API client
challenges = []
with openchallenges_client.ApiClient(configuration) as api_client:
    api_instance = challenge_api.ChallengeApi(api_client)

    # IMPORTANT: The auto-generated API client does not support object for query params
    query = openchallenges_client.ChallengeSearchQuery(page_number=0, page_size=1000)

    try:
        # Get the first page of the list of challenges
        page = api_instance.list_challenges(query)
        challenges.extend(page.challenges)
    except openchallenges_client.ApiException as e:
        print("Exception when calling ChallengeApi->list_challenges: %s\n" % e)

# Sort challenge by ID in ascending order
challenges.sort(key=lambda challenge: challenge.id, reverse=False)


# GENERATE THE HEADLINES WITH AWS BEDROCK

# Configure the Bedrock client

import json
import os
import sys

import boto3
import botocore

module_path = "src"
sys.path.append(os.path.abspath(module_path))
from utils import bedrock, print_ww

os.environ["AWS_DEFAULT_REGION"] = "us-east-1"
os.environ["AWS_PROFILE"] = "cnb"

boto3_bedrock = bedrock.get_bedrock_client(
    assumed_role=os.environ.get("BEDROCK_ASSUME_ROLE", None),
    region=os.environ.get("AWS_DEFAULT_REGION", None),
)

# Configure base model options

from langchain.llms.bedrock import Bedrock

inference_modifier = {
    "max_tokens_to_sample": 6000,
    "temperature": 0.6,
    "top_k": 250,
    "top_p": 1,
    "stop_sequences": ["\n\nHuman"],
}

textgen_llm = Bedrock(
    model_id="anthropic.claude-v2",
    client=boto3_bedrock,
    model_kwargs=inference_modifier,
)


def generate_challenge_headlines(text, num_headlines):
    prompt = (
        f"Please generate {num_headlines} headlines that have less than 80 characters from the "
        "following challenge description. "
        "The headlines must summarize the goal of the challenge. "
        # "The headlines must not include the name of the challenge. "
        "The headlines must reads naturally. "
        f"Description: \n{text}"
    )
    response = Bedrock(
        model_id="anthropic.claude-v2",
        client=boto3_bedrock,
        model_kwargs=inference_modifier,
    )(prompt)
    return response


from itertools import compress
import json


def is_raw_headline(raw_headline):
    prefixes = ("1. ", "2. ", "3. ", "4. ", "5. ")
    return raw_headline.startswith(prefixes)


def process_challenge(challenge):
    print(f"Processing challenge ID {challenge.id}: {challenge.name}")
    response = generate_challenge_headlines(challenge.description, 5)

    raw_headlines = response.splitlines()
    headlines = list(compress(raw_headlines, map(is_raw_headline, raw_headlines)))

    obj = {
        "id": challenge.id,
        "slug": challenge.slug,
        "name": challenge.name,
        "headline": challenge.headline,
        "headline_alternatives": headlines,
    }
    return obj


challenge_headlines = list(map(process_challenge, challenges))


# SAVE OUTPUT TO FILE

with open("challenge_headlines.json", "w") as f:
    json.dump(challenge_headlines, f, indent=2)
