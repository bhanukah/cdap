
-- Train Model
python -m rasa_nlu.train -c nlu_config.yml --data traindat.json -o models --fixed_model_name nlu --project current --verbose


-- Test
python -W ignore predict.py "user message"