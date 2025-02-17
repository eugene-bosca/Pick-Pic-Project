import firebase_admin
from firebase_admin import credentials, auth

# Path to your Firebase service account JSON file
cred = credentials.Certificate("concrete-spider-449820-p0-b07324f24234.json")
firebase_admin.initialize_app(cred)