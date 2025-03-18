from django.conf import settings
from django.http import JsonResponse

from firebase_admin import auth
import jwt

SECRET_KEY = settings.SECRET_KEY  # Use Django's secret key

class ExceptionMiddleware:
    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        return self.get_response(request)
    
    def process_exception(self, request, exception):
        return JsonResponse({"error": "Internal server error", "details": str(exception)}, status=500)

class AuthMiddleware:
    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        
        if request.path in ["/swagger", "/swagger/", "/schema", "/schema/"]:
            return self.get_response(request)

        auth_header = request.headers.get("Authorization")

        if not auth_header:
            return JsonResponse({"error": "Authorization header is missing"}, status=401)

        try:
            scheme, token = auth_header.split(" ")
            if scheme.lower() != "bearer":
                return JsonResponse({"error": "Invalid authentication scheme"}, status=401)

            # Verify Firebase Token
            try:
                decoded_token = auth.verify_id_token(token)
            except:
                # Verify Custom JWT Token
                try:
                    decoded_token = jwt.decode(token, SECRET_KEY, algorithms=["HS256"])
                    request.user = decoded_token["username"]
                except jwt.ExpiredSignatureError:
                    return JsonResponse({"error": "Token has expired"}, status=401)
                except jwt.InvalidTokenError:
                    return JsonResponse({"error": "Invalid token"}, status=401)

        except ValueError:
            return JsonResponse({"error": "Invalid Authorization header format"}, status=401)

        return self.get_response(request)
