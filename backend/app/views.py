from rest_framework import viewsets
from .models import *
from .serializers import UserSerializer, UserSettingsSerializer, EventSerializer, EventUserSerializer, ImageSerializer, EventContentSerializer, ScoredBySerializer
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from drf_spectacular.utils import extend_schema, OpenApiParameter, OpenApiExample
import base64

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
    def retrieve(self, request, *args, **kwargs):
        event_id = kwargs.get(self.lookup_field)
        queryset = self.queryset.filter(event_id=event_id)

        if not queryset.exists():
            return Response(data={[]}, status=status.HTTP_200_OK)

        serializer = self.get_serializer(queryset, many=True)
        return Response(data=serializer.data, status=status.HTTP_200_OK)

# Image ViewSet
class ImageViewSet(viewsets.ModelViewSet):
    queryset = Image.objects.all()
    serializer_class = ImageSerializer
    lookup_field = "image_id"

    def retrieve(self, request, *args, **kwargs):
        image_id = kwargs.get(self.lookup_field)
        queryset = self.queryset.filter(image_id=image_id)

        if not queryset.exists():
            return Response(data={[]}, status=status.HTTP_200_OK)

        serializer = self.get_serializer(queryset, many=True)
        return Response(data=serializer.data, status=status.HTTP_200_OK)

# EventContent ViewSet
class EventContentViewSet(viewsets.ModelViewSet):
    queryset = EventContent.objects.all()
    serializer_class = EventContentSerializer
    lookup_field = "event_id"

    def retrieve(self, request, *args, **kwargs):
        event_id = kwargs.get(self.lookup_field)
        queryset = self.queryset.filter(event_id=event_id)

        if not queryset.exists():
            return Response(data=[], status=status.HTTP_200_OK)

        serializer = self.get_serializer(queryset, many=True)
        return Response(data=serializer.data, status=status.HTTP_200_OK)

# ScoredBy ViewSet
class ScoredByViewSet(viewsets.ModelViewSet):
    queryset = ScoredBy.objects.all()
    serializer_class = ScoredBySerializer

# Secret key for JWT
SECRET_KEY = settings.SECRET_KEY  # Use Django's secret key

@api_view(['GET'])
def event_info(request: Request, event_id):
    event = Event.objects.get(event_id=event_id)
    return Response(data=EventSerializer(event).data, status=status.HTTP_200_OK)

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
 
@api_view(['GET', 'DELETE'])
def get_delete_image(request: Request, event_id=None, image_id=None):
    try:
        if request.method == 'GET':
            event_exists = Event.objects.filter(event_id=event_id).exists()

            if not event_exists:
                return Response(status=status.HTTP_404_NOT_FOUND, data={ "error":"event-image pair not found" })

            filename = Image.objects.get(image_id=image_id).file_name

            file_bytes = download_from_gcs('pick-pic', filename)

            file_stream = io.BytesIO(file_bytes)

            content_type, _ = mimetypes.guess_type(filename)
            if content_type is None:
                content_type = "application/octet-stream"

            return FileResponse(file_stream, filename=filename, content_type=content_type, status=status.HTTP_200_OK)            
        elif request.method == 'DELETE':
                event_exists = Event.objects.filter(event_id=event_id).exists()

                if not event_exists:
                    return Response(status=status.HTTP_404_NOT_FOUND, data={ "error":"event-image pair not found" })

                filename = Image.objects.get(image_id=image_id).file_name

                event_content = EventContent.objects.get(event_id=event_id, image_id=image_id)

                event_content.delete()

                delete_from_gcs('pick-pic', filename)

                return Response(status=status.HTTP_202_ACCEPTED, data={})
        else:
            return Response(data={ "error":"only support GET, PUT, and DELETE." }, status=status.HTTP_403_FORBIDDEN)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@api_view(['PUT'])
def create_image(request: Request, event_id=None):
    try:
        if not Event.objects.filter(event_id=event_id).exists():
            return Response(status=status.HTTP_404_NOT_FOUND, data={ "error":"event not found" }) 

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

        new_image = Image.objects.create(file_name=unique_name)
        
        event_content = EventContent.objects.create(event_id=event_id, image_id=(new_image.image_id))

        return Response(status=status.HTTP_201_CREATED, data=EventContentSerializer(event_content).data)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@api_view(['GET', 'PUT'])
def user_pfp(request: Request):
    try:
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
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@api_view(['GET'])
def event_image_count(request: Request, event_id):
    try:
        count = EventContent.objects.filter(event_id=event_id).count()
        return Response(data={ "image_count": count }, status=status.HTTP_200_OK)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@api_view(['GET'])
def list_users_events(request: Request, user_id):
    try:
        owned_events = Event.objects.filter(owner_id=user_id)

        invited_event_ids = EventUser.objects.filter(user_id=user_id, accepted = True).values_list('event_id', flat=True)

        invited_events = Event.objects.filter(event_id__in=invited_event_ids)
        
        return Response(status=status.HTTP_200_OK ,data={
            "owned_events": EventSerializer(owned_events, many=True).data,
            "invited_events": EventSerializer(invited_events, many=True).data
        })
    except Event.DoesNotExist:
        return Response({'error': 'Event could not be found.'}, status=status.HTTP_404_NOT_FOUND)
    except EventUser.DoesNotExist:
        return Response({'error': 'Event User could not be found.'}, status=status.HTTP_404_NOT_FOUND)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
 
@extend_schema(
    request=EventSerializer,
    responses={201: EventSerializer, 404: {"error": "user not found"}},
    examples=[
        OpenApiExample(
            name="Create Event Example",
            value={"user_id": 1, "event_name": "Django Meetup"},
            request_only=True,
        ),
    ],
)
@api_view(['POST'])
def create_new_event(request: Request):

    user_id = request.data.get('user_id')
    event_name = request.data.get('event_name')

    event_owner = User.objects.get(user_id=user_id)

    event = Event.objects.create(event_name=event_name, owner=event_owner)

    EventUser.objects.create(event_id=event.event_id, user_id=user_id)

    return Response(status=status.HTTP_201_CREATED, data=EventSerializer(event).data)


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

    try:
        user = User.objects.get(firebase_id=firebase_id)
        return Response({'user_id': user.user_id}, status=status.HTTP_200_OK) # Convert UUID to string for JSON serialization
    except User.DoesNotExist:
        return Response({'error': 'User not found'}, status=status.HTTP_404_NOT_FOUND)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@api_view(['POST'])
def add_user_to_event(request: Request, event_id, user_id):
    """
    Adds a user to an event.

    Args:
        request: The HTTP request object.
        event_id: The UUID of the event.
        user_id: The UUID of the user.

    Returns:
        Response: A JSON response indicating success or failure.
    """
    try:
        # Convert UUID strings to UUID objects
        event_id = uuid.UUID(str(event_id))
        user_id = uuid.UUID(str(user_id))

        # Check if the event and user exist
        event = Event.objects.get(event_id=event_id)
        user = User.objects.get(user_id=user_id)

        # Check if the user is already in the event
        if EventUser.objects.filter(event_id=event_id, user_id=user_id).exists():
            return Response({'error': 'User already in event'}, status=status.HTTP_400_BAD_REQUEST)

        # Add the user to the event
        EventUser.objects.create(event_id=event_id, user_id=user_id)

        return Response({'message': 'User added to event successfully'}, status=status.HTTP_201_CREATED)

    except Event.DoesNotExist:
        return Response({'error': 'Event not found'}, status=status.HTTP_404_NOT_FOUND)
    except User.DoesNotExist:
        return Response({'error': 'User not found'}, status=status.HTTP_404_NOT_FOUND)
    except ValueError:
        return Response({'error': 'Invalid UUID format'}, status=status.HTTP_400_BAD_REQUEST)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@api_view(['GET'])
def get_user_id_from_email(request: Request, email):
    """
    Retrieves the user_id associated with a given email address.

    Args:
        request: The HTTP request object.
        email: The email address to search for.

    Returns:
        Response: A JSON response containing the user_id or an error message.
    """
    try:
        user = User.objects.get(email=email)
        return Response({'user_id': str(user.user_id)}, status=status.HTTP_200_OK) # Convert UUID to string for JSON serialization
    except User.DoesNotExist:
        return Response({'error': 'User not found'}, status=status.HTTP_404_NOT_FOUND)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    
@api_view(['GET'])
def get_highest_scored_image(request: Request, event_id):
    """
    Retrieves the image with the highest score for a given event.

    Args:
        request: The HTTP request object.
        event_id: The UUID of the event.

    Returns:
        Response: A FileResponse containing the image or an error message.
    """
    try:
        event_id = uuid.UUID(str(event_id))
        
        # Find the image with the highest score in the event
        highest_scored_image = Image.objects.filter(
            eventcontent__event_id=event_id
        ).order_by('-score').first()

        if not highest_scored_image:
            return Response({'error': 'No images found for this event'}, status=status.HTTP_404_NOT_FOUND)

        file_name = highest_scored_image.file_name

        if not file_name:
          return Response({'error': 'Image file name not set'}, status=status.HTTP_404_NOT_FOUND)

        file_bytes = download_from_gcs('pick-pic', file_name)
        file_stream = io.BytesIO(file_bytes)

        content_type, _ = mimetypes.guess_type(file_name)
        if content_type is None:
            content_type = "application/octet-stream"

        return FileResponse(file_stream, content_type=content_type, status=status.HTTP_200_OK)

    except ValueError:
        return Response({'error': 'Invalid UUID format'}, status=status.HTTP_400_BAD_REQUEST)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    
@api_view(['DELETE'])
def remove_event_user(request, event_id, user_id):
    """
    Removes a user from an event.
    """
    try:
        event_user = EventUser.objects.get(event__event_id=event_id, user__user_id=user_id)
        event_user.delete()
        return Response(status=status.HTTP_200_OK)
    except EventUser.DoesNotExist:
        return Response({'error': 'Event user not found.'}, status=status.HTTP_404_NOT_FOUND)

@api_view(['PUT'])
def accept_event_user(request, event_id, user_id):
    """
    Changes the 'accepted' status of an EventUser to True.
    """
    try:
        event_user = EventUser.objects.get(event__event_id=event_id, user__user_id=user_id)
        event_user.accepted = True
        event_user.save()
        return Response({'message': 'Event user accepted successfully.'}, status=status.HTTP_200_OK)
    except EventUser.DoesNotExist:
        return Response({'error': 'Event user not found.'}, status=status.HTTP_404_NOT_FOUND)
    
@api_view(['GET'])
def get_pending_events(request, user_id):
    """
    Retrieves a list of Event objects where the user is invited but has not accepted.
    """
    try:
        pending_events = EventUser.objects.filter(user__user_id=user_id, accepted=False)
        events = [pe.event for pe in pending_events]
        serializer = EventSerializer(events, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@api_view(['GET'])
def event_last_modified(request, event_id):
    event = Event.objects.filter(event_id=event_id)
    if not event.exists():
        return Response(status=status.HTTP_404_NOT_FOUND, data={ "error":"user not found" })
    return Response(data={"last_modified": Event.objects.get(event_id=event_id).last_modified.strftime("%d/%m/%Y, %H:%M:%S") },
                    status=status.HTTP_200_OK)

@api_view(['DELETE'])
def user_delete_event(request, user_id, event_id):
    try:
        event = Event.objects.get(event_id=event_id, user_id=user_id)
        event.delete()
        return Response(status=status.HTTP_202_ACCEPTED)
    except Event.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND, data={ "error":"user does not own this event" })

@api_view(['DELETE'])
def remove_user_from_event(request, event_id, user_id):
    try:
        event_user = EventUser.objects.get(event_id=event_id, user_id=user_id)
        event_user.delete()
        return Response(status=status.HTTP_202_ACCEPTED)
    except EventUser.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND, data={ "error":"event does not exist or user is not part of this event" })


@api_view(['POST'])
def invite_to_event(request, event_id):
    """
    Invite one or more users to an event (in-app method).
    Expects a payload with:
    {
        "user_id": ["uuid1", "uuid2", ...] or "user_id": "uuid"
    }
    """
    try:
        # Get the event
        event = Event.objects.get(event_id=event_id)

        # Check if the request has multiple user_ids or a single user_id
        user_ids = request.data.get('user_id', [])

        if isinstance(user_ids, dict):
            user_ids = []
        elif isinstance(user_ids, str):
            user_ids = [user_ids]
        elif not isinstance(user_ids, list):
            user_ids = []

        if not user_ids:
            return Response({'error': 'No user_ids provided'}, status=status.HTTP_400_BAD_REQUEST)

        invited_users = []

        for user_id in user_ids:
            if User.objects.filter(user_id=user_id).exists():
                # Create an EventUser with accepted=False
                _, created = EventUser.objects.get_or_create(
                    event_id=event_id,
                    user_id=user_id,
                    defaults={'accepted': False}
                )

            if created:
                invited_users.append(user_id)

        return Response({
            'message': f'Successfully invited {len(invited_users)} users',
            'invited_users': UserSerializer(invited_users).data
        }, status=status.HTTP_201_CREATED)

    except Event.DoesNotExist:
        return Response({'error': 'Event not found'}, status=status.HTTP_404_NOT_FOUND)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

# Generate invite link with obfuscated event ID
@api_view(['GET'])
def generate_invite_link(request, event_id):
    """
    Generate a shareable invite link using the event's obfuscated ID.
    """
    try:
        # Get the event to make sure it exists
        event = Event.objects.get(event_id=event_id)
        
        # Ensure obfuscated_event_id exists
        if not event.obfuscated_event_id:
            # Generate a new UUID for the obfuscated ID
            event.obfuscated_event_id = uuid.uuid4()
            event.save()
        
        # Create invite link using the obfuscated event ID
        base_url = settings.INVITE_BASE_URL if hasattr(settings, 'INVITE_BASE_URL') else request.build_absolute_uri('/join/')
        invite_link = f"{base_url.rstrip('/')}/{event.obfuscated_event_id}"
        
        return Response({
            'invite_link': invite_link,
            'obfuscated_event_id': str(event.obfuscated_event_id)
        }, status=status.HTTP_200_OK)
    except Event.DoesNotExist:
        return Response({'error': 'Event not found'}, status=status.HTTP_404_NOT_FOUND)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

# Join event via link/QR code
@api_view(['GET', 'POST'])
def join_via_link(request, obfuscated_event_id):
    """
    Handle user joining an event via an invite link with jibberish event ID.
    GET: Return event details
    POST: Join the event
    """
    try:
        # Find the event using the obfuscated ID
        event = Event.objects.get(obfuscated_event_id=obfuscated_event_id)

        if request.method == 'GET':
            # Return event details
            serializer = EventSerializer(event)
            return Response(serializer.data, status=status.HTTP_200_OK)

        elif request.method == 'POST':
            # Get the user from the request
            user_id = request.data.get('user_id')
            if not user_id:
                return Response({'error': 'User ID is required'}, status=status.HTTP_400_BAD_REQUEST)

            # Check if the user already has a relation with this event
            event_user, created = EventUser.objects.get_or_create(
                event_id=event.event_id,
                user_id=user_id,
                defaults={'accepted': True}  # Auto-accept when joining via link
            )

            if not created:
                # Update the existing record to accepted if needed
                if not event_user.accepted:
                    event_user.accepted = True
                    event_user.save()

            return Response({
                'message': 'Successfully joined the event',
                'event': EventSerializer(event).data
            }, status=status.HTTP_200_OK)

    except Event.DoesNotExist:
        return Response({'error': 'Invalid invite link'}, status=status.HTTP_404_NOT_FOUND)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

# Handle invitation response (accept/decline)
@api_view(['POST'])
def handle_invitation(request, event_id, action):
    """
    Handle a user accepting or declining an invitation.
    action can be 'accept' or 'decline'
    """
    try:
        user_id = request.data.get('user_id')
        if not user_id:
            return Response({'error': 'User ID is required'}, status=status.HTTP_400_BAD_REQUEST)

        # Get the event user record
        event_user = EventUser.objects.get(event_id=event_id, user_id=user_id)

        if action == 'accept':
            # Accept the invitation
            event_user.accepted = True
            event_user.save()
            return Response({'message': 'Invitation accepted'}, status=status.HTTP_200_OK)

        elif action == 'decline':
            # Decline by removing the record
            event_user.delete()
            return Response({'message': 'Invitation declined'}, status=status.HTTP_200_OK)

        else:
            return Response({'error': 'Invalid action'}, status=status.HTTP_400_BAD_REQUEST)

    except EventUser.DoesNotExist:
        return Response({'error': 'User was not invited to event'}, status=status.HTTP_404_NOT_FOUND)
    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

# Get pending event invitations for a user
@api_view(['GET'])
def get_pending_event_invitations(request, user_id):
    """
    Get all pending event invitations for a user.
    """
    try:
        # Find all events where the user has been invited but not accepted
        pending_invitations = EventUser.objects.filter(
            user_id=user_id,
            accepted=False
        )

        # Prepare the response data
        events_data = []
        for invitation in pending_invitations:
            event_data = EventSerializer(invitation.event).data
            # Add owner - for something like "Luda has invited you to his event awesome cookies photos"
            event_data['owner_name'] = invitation.event.owner.display_name
            events_data.append(event_data)

        return Response(events_data, status=status.HTTP_200_OK)

    except Exception as e:
        return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)