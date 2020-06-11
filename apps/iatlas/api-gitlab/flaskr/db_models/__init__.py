from flaskr import db

Base = db.Model

from .copy_number_result import CopyNumberResult
from .driver_result import DriverResult
from .edge import Edge
from .edge_to_tag import EdgeToTag
from .feature import Feature
from .feature_class import FeatureClass
from .feature_to_sample import FeatureToSample
from .gene import Gene
from .gene_family import GeneFamily
from .gene_function import GeneFunction
from .gene_to_sample import GeneToSample
from .gene_to_type import GeneToType
from .gene_type import GeneType
from .immune_checkpoint import ImmuneCheckpoint
from .method_tag import MethodTag
from .mutation import Mutation
from .mutation_code import MutationCode
from .mutation_type import MutationType
from .node import Node
from .node_to_tag import NodeToTag
from .node_type import NodeType
from .pathway import Pathway
from .patient import Patient
from .sample import Sample
from .sample_to_mutation import SampleToMutation
from .sample_to_tag import SampleToTag
from .slide import Slide
from .super_category import SuperCategory
from .tag import Tag
from .tag_to_tag import TagToTag
from .therapy_type import TherapyType
