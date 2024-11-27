import json
import pytest


def test_test_query(client):
    query = """query Test {
        test {
            items {
                contentType
                userAgent
                headers {
                    contentLength
                    contentType
                    host
                    userAgent
                }
            }
            page
        }
    }"""
    response = client.post('/api', json={'query': query})
    json_data = json.loads(response.data)
    test = json_data['data']['test']
    results = test['items']

    assert type(results['contentType']) is str
    assert type(results['userAgent']) is str
    assert type(results['headers']['contentLength']) is int
    assert type(results['headers']['contentType']) is str
    assert type(results['headers']['host']) is str
    assert type(results['headers']['userAgent']) is str
