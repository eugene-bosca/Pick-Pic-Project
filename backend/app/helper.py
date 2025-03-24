from firebase_admin import auth

def getUserFromToken(jwt):
    try:
        decoded_token = auth.verify_id_token(jwt)
        uid = decoded_token.get("uid")
        print(f"Firebase UID: {uid}")
        return uid
    except Exception:
        return None