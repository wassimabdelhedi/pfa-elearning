import urllib.request
import json

url = "http://localhost:8081/api/messages/contacts/search?query=amin"
req = urllib.request.Request(url)
# How to get auth token? We don't have one here easily
# We can just print the error if it's a 401, or try an endpoint that might tell us if it's up.
