from flask import Flask
from flask import request, abort
from influxdb_client import InfluxDBClient
from datetime import datetime

app = Flask(__name__)

bucket = "Heartbeat2809"
org = "Heartbeat"

f = open("token.txt", "r")
token: str = f.read()
f.close()

f = open("client_token.txt", "r")
client_token: str = f.read()
f.close()

used_labels = []


class InfluxClient:
    def __init__(self, token, org, bucket):
        self._org = org
        self._bucket = bucket
        self._client = InfluxDBClient(url="http://localhost:8086", token=token)

    from influxdb_client.client.write_api import ASYNCHRONOUS

    def write_data(self, data, write_option=ASYNCHRONOUS):
        write_api = self._client.write_api(write_option)
        write_api.write(self._bucket, self._org, data, write_precision="ms")

    def delete_data(self, start, end, predicate):
        delete_api = self._client.delete_api()
        return delete_api.delete(start, end, predicate, self._bucket, self._org)


IC = InfluxClient(token, org, bucket)


# Adding measurement
@app.route("/new_measurement", methods=['POST'])
def new_measurements():
    args = request.args
    args_token = args.get("token", '')
    if args_token == client_token:
        content = request.json
        print("from " + str(content[0]["time"]) + "to" + str(content[-1]["time"]))
        print(len(content))
        label = content[0]["measurement"]
        used_labels.append(label)
        IC.write_data(
            content
        )
        return content
    else:
        abort(401)


@app.route("/remove_measurement", methods=['DELETE'])
def remove_measurement():
    args = request.args
    args_token = args.get("token", '')
    if args_token == client_token:
        measurement = args.get("label", '')
        if measurement != "diary":
            predicate = ('_measurement="' + measurement + '"')
            start = datetime(1970, 1, 1, 0, 0, 0)
            stop = datetime.now()
            IC.delete_data(start, stop, predicate)
            return measurement + " deleted"
        else:
            abort(403)
    else:
        abort(401)


# Diary entries
@app.route("/new_entry", methods=['POST'])
def new_entry():
    content = request.json

    label = content["label"]
    description = content["description"]
    time = content["time"]

    IC.write_data(
        [
            {
                "measurement": "diary",
                "tags": {

                },
                "fields": {
                    "label": label,
                    "description": description,
                },
                "time": int(time * 1000)
            }
        ]
    )

    return content


# Checking if label is available
@app.route("/is_available", methods=['GET'])
def is_available():
    args = request.args
    print(used_labels)
    print(args.get("label", ''))
    if args.get("label", '') in used_labels:
        return "False"
    else:
        return "True"
