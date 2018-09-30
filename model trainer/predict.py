from rasa_nlu.model import Interpreter
import json
import sys
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

message = message = "lost my driving license"
if len(sys.argv) > 1:
	message = sys.argv[1];

interpreter = Interpreter.load("./models/current/nlu")
result = interpreter.parse(message)
print(json.dumps(result, indent=2))