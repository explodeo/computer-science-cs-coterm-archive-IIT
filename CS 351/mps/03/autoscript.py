import webbrowser
payload = {
	"UserID1": "cmorcom",
	"Password1": "jdm12genius"
}

import requests
from lxml import html

session_requests = requests.session()
login_url = "https://whentowork.com/logins.htm"
result = session_requests.get(login_url)

tree = html.fromstring(result.text)
authenticity_token = list(set(tree.xpath("//input[@name='csrfmiddlewaretoken']/@value")))#[0]
print(authenticity_token)

result = session_requests.post(
	login_url, 
	data = payload, 
	headers = dict(referer=login_url)
)