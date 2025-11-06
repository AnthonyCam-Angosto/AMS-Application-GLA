FROM python:latest

COPY /app ./app

RUN pip install --no-cache-dir -r app/requirements.txt

CMD  ["python", "./app/main.py"]