from django.conf import settings
from django.http import JsonResponse

from firebase_admin import auth

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
        #return self.get_response(request)

        if request.path in ["/swagger", "/swagger/", "/schema", "/schema/", "/", "/favicon.ico"]:
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
                auth.verify_id_token(token)
            except:
                return JsonResponse({"error": "token unauthorized"}, status=401)

        except ValueError:
            return JsonResponse({"error": "Invalid Authorization header format"}, status=401)

        return self.get_response(request)
