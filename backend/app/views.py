from rest_framework import viewsets
from .models import *
from .serializers import UserSerializer, UserSettingsSerializer, EventSerializer, EventUserSerializer, ImageSerializer, EventContentSerializer, ScoredBySerializer
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from drf_spectacular.utils import extend_schema, OpenApiParameter, extend_schema_view, OpenApiTypes, OpenApiResponse, OpenApiExample

from .models import User

from django.http import FileResponse
from django.contrib.auth.hashers import check_password
from django.conf import settings
from rest_framework.request import Request

import uuid
import jwt
from .google_cloud_storage.bucket import *
from datetime import datetime
import io
import mimetypes

# User ViewSet
class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    
# UserSettings ViewSet
class UserSettingsViewSet(viewsets.ModelViewSet):
    queryset = UserSettings.objects.all()
    serializer_class = UserSettingsSerializer

# Event ViewSet
class EventViewSet(viewsets.ModelViewSet):
    queryset = Event.objects.all()
    serializer_class = EventSerializer


# EventUser ViewSet
class EventUserViewSet(viewsets.ModelViewSet):
    queryset = EventUser.objects.all()
    serializer_class = EventUserSerializer
    lookup_field = "event_id"

# Image ViewSet
class ImageViewSet(viewsets.ModelViewSet):
    queryset = Image.objects.all()
    serializer_class = ImageSerializer

# EventContent ViewSet
class EventContentViewSet(viewsets.ModelViewSet):
    queryset = EventContent.objects.all()
    serializer_class = EventContentSerializer
    lookup_field = "event_id"

# ScoredBy ViewSet
class ScoredByViewSet(viewsets.ModelViewSet):
    queryset = ScoredBy.objects.all()
    serializer_class = ScoredBySerializer

class CreateNewEventViewSet(viewsets.ViewSet):
    serializer = EventSerializer

# Secret key for JWT
SECRET_KEY = settings.SECRET_KEY  # Use Django's secret key

@api_view(['POST'])
def authenticate(request: Request):
    username = request.data.get('username')
    password = request.data.get('password')

    if not username or not password:
        return Response({"error": "Username and password are required"}, status=400)

    try:
        user = User.objects.get(username=username)
        
        # Validate password securely
        if check_password(password, user.password):
            # Generate JWT token
            payload = {
                "id": user.id,
                "username": user.username,
                "exp": datetime.datetime.utcnow() + datetime.timedelta(hours=1),  # Token expires in 1 hour
                "iat": datetime.datetime.utcnow(),
            }
            token = jwt.encode(payload, SECRET_KEY, algorithm="HS256")

            return Response({
                "exists": True,
                "token": token,
                "comment": "Authentication successful"
            })
        else:
            return Response({"exists": False, "comment": "Incorrect password"}, status=401)

    except User.DoesNotExist:
        return Response({"exists": False, "comment": "User does not exist"}, status=404)
    except User.MultipleObjectsReturned:
        return Response({"exists": False, "comment": "Multiple users with the same username exist???"}, status=500)

@extend_schema_view(
    get=extend_schema(
        parameters=[
            OpenApiParameter("picture_name", str, OpenApiParameter.QUERY, description="picture name"),
        ],
        responses={200: OpenApiResponse(
            description="File download",
        )}
    ),
    post=extend_schema(
        request={
            "image/jpeg": OpenApiTypes.BINARY,
            "image/png": OpenApiTypes.BINARY,
        },
        responses={204: None}
    )
)    
@api_view(['GET', 'PUT'])
def picture(request: Request):
    if request.method == 'GET':
        
        picture_name = request.GET.get("picture_name")

        file_bytes = download_from_gcs('pick-pic', picture_name)

        file_stream = io.BytesIO(file_bytes)

        content_type, _ = mimetypes.guess_type(picture_name)
        if content_type is None:
            content_type = "application/octet-stream"

        return FileResponse(file_stream, content_type=content_type, status=status.HTTP_200_OK)

    elif request.method == 'PUT':
        
        file_bytes = request.body
        content_type = request.headers.get('Content-Type') 
        unique_name = datetime.now().strftime("%Y%m%d%H%M%S%f")

        if content_type == 'image/jpeg':
            unique_name += '.jpeg'
        elif content_type == 'image/png':
            unique_name += '.png'
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST, data={ "error": "no header - Content-Type" })

        upload_to_gcs('pick-pic', file_bytes, unique_name, content_type)

        return Response(status=status.HTTP_201_CREATED)
    
@api_view(['GET', 'PUT'])
def user_pfp(request: Request):

    user_id = request.GET.get("user_id")

    if not user_id:
        return Response({"error": "User ID is required"}, status=400)
    
    user_id = uuid.UUID(user_id)

    if request.method == 'GET':
        
        file_name = User.objects.get(user_id=user_id).profile_picture
        
        file_bytes = download_from_gcs('pick-pic', file_name)
        file_stream = io.BytesIO(file_bytes)

        content_type, _ = mimetypes.guess_type(file_name)
        if content_type is None:
            content_type = "application/octet-stream"

        return FileResponse(file_stream, content_type=content_type, status=status.HTTP_200_OK)

    elif request.method == 'PUT':
        file_bytes = request.body
        content_type = request.headers.get('Content-Type') 
        unique_name = datetime.now().strftime("%Y%m%d%H%M%S%f")

        if content_type == 'image/jpeg':
            unique_name += '.jpeg'
        elif content_type == 'image/png':
            unique_name += '.png'
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST, data={ "error": "no header - Content-Type" })

        upload_to_gcs('pick-pic', file_bytes, unique_name, content_type)

        User.objects.get(user_id=user_id).profile_picture = unique_name

        return Response(status=status.HTTP_201_CREATED)
    
@api_view(['GET'])
def event_image_count(request: Request, event_id):

    count = EventContent.objects.filter(event_id=event_id).count()

    return Response(data={ "image_count": count }, status=status.HTTP_200_OK)

@api_view(['GET'])
def list_users_events(request: Request, user_id):

    owned_events = Event.objects.filter(owner_id=user_id)

    invited_event_ids = EventUser.objects.filter(user_id=user_id).values_list('event_id', flat=True)

    invited_events = Event.objects.filter(event_id__in=invited_event_ids)
    
    return Response(status=status.HTTP_200_OK ,data={
        "owned_events": EventSerializer(owned_events, many=True).data,
        "invited_events": EventUserSerializer(invited_events, many=True).data
    })
 
@api_view(['POST'])
def create_new_event(request: Request):

    user_id = request.data.get('user_id')
    event_name = request.data.get('event_name')

    event = Event.objects.create(event_name=event_name, user_id=user_id)

    eventUser = EventUser.objects.create(event_id=event.event_id, user_id=user_id)

    return Response(status=status.HTTP_201_CREATED, data=event)

@api_view(['POST'])
def invite_to_event(request: Request):

    user_id = request.data.get('user_id')
    event_id = request.data.get('event_id') 

    eventUser = EventUser.objects.create(event_id=event_id, user_id=user_id)

    return Response(status=status.HTTP_202_ACCEPTED, data=eventUser)

@api_view(['GET'])
def get_user_id_by_firebase_id(request: Request, firebase_id):
    """
    Retrieves the user_id associated with a given firebase_id.

    Args:
        request: The HTTP request object.
        firebase_id: The firebase_id to search for.

    Returns:
        Response: A JSON response containing the user_id or an error message.
    """

    print(firebase_id)
    print(User.objects.all())

    try:
        user = User.objects.get(firebase_id=firebase_id)
        return Response({'user_id': user.user_id}, status=status.HTTP_200_OK) # Convert UUID to string for JSON serialization
    except User.DoesNotExist:
        return Response({'error': 'User not found'}, status=status.HTTP_404_NOT_FOUND)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)