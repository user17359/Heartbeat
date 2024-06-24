import requests

from datetime import datetime
from rich import print
import socket

from bt.app.data_classes.diary_entry import DiaryEntry


def send_entry(entry: DiaryEntry, config):
    # API endpoint
    url = 'https://' + config["server_address"] + ':5000/new_entry'

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    result = sock.connect_ex((config["server_address"], 5000))

    if result == 0:
        now = datetime.now()
        entry_time = datetime(now.year,
                              now.month,
                              now.day,
                              entry.hour,
                              entry.minute)
        payload = {"label": entry.label, "description": entry.description, "time": entry_time.timestamp()}
        response = requests.post(url, json=payload)
        print("Response [blue]" + str(response.status_code) + "[/blue]")
    else:
        print("[red]Server can't be reached[/red]")
