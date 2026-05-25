$ErrorActionPreference = "Stop"

Set-Location -Path $PSScriptRoot

py -3 -m pip install -r requirements.txt
py -3 app.py
