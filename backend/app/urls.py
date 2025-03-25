"""
URL configuration for PickPic project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""

from django.contrib import admin
from django.urls import path, include
from rest_framework.routers import DefaultRouter
from drf_spectacular.views import SpectacularAPIView, SpectacularSwaggerView

from . import views

router = DefaultRouter()
router.register(r'user', views.UserViewSet)
router.register(r'user_settings', views.UserSettingsViewSet)
# router.register(r'event', views.EventViewSet)
router.register(r'event_users', views.EventUserViewSet)
# router.register(r'images', views.ImageViewSet)
# router.register(r'event/content', views.EventContentViewSet)
# router.register(r'scored_by', views.ScoredByViewSet)

urlpatterns = [
    # swagger
    path('schema/', SpectacularAPIView.as_view(), name='schema'),
    path('swagger/', SpectacularSwaggerView.as_view(url_name='schema'), name='swagger-ui'),

    # custom CRUD endpoints
    path('', include(router.urls)),

    # image/picture endpoints
    path('event/<str:event_id>/image/<str:image_id>/', views.get_delete_image, name='GET/DELETE Image'),
    #path('event/<str:event_id>/image/count/', views.event_image_count, name='Event Image Count'),
    path('event/<str:event_id>/image/highest_score/', views.get_highest_scored_image, name='Get Highest Score Image'),
    path('event/<str:event_id>/image/user/<str:user_id>/unranked/', views.unranked_images, name='GET unranked images'),
    path('event/<str:event_id>/image/', views.create_image, name='PUT Image'),
    
    # upvote/downvote
    path('event/<str:event_id>/image/<str:image_id>/vote/', views.vote_image, name='Upvote/Downvote Image'),
 
    # event
    path('event/create/', views.create_new_event, name='Create New Event'),
    path('event/<str:event_id>/', views.event_info, name='Event Info'),
    path('event/<str:event_id>/content/', views.EventContentViewSet.as_view({'get': 'list'}), name='Event Content'),
    path('event/<str:event_id>/last_modified/', views.event_last_modified, name='Event Last Modified Timestamp'),

    # event users
    path('event/<str:event_id>/users/', views.EventUserViewSet.as_view({'get': 'list'}), name='Event Users'),
    path('event/<str:event_id>/pending_users/', views.pending_invites, name='Pending Event Users'),
    path('event/<str:event_id>/user/<str:user_id>/', views.remove_event_user, name='Remove Event User'),

    path('user/<str:user_id>/events/', views.list_users_events, name='List Users Events'),
    path('user/<str:user_id>/events/<str:event_id>/', views.user_delete_event, name='Delete User\'s own Album'),

    path('user/id/from_fire_base/', views.get_user_id_by_firebase_id, name='Exchange User ID For Firebase ID'),
    path('user/id/from_email/', views.get_user_id_from_email, name='Get User IDs From Emails'),

    # View pending invitations for a user
    path('user/<str:user_id>/pending_event_invitations/', views.get_pending_event_invitations, name='get_pending_event_invitations'),

    # creates pending invite
    path('event/<str:event_id>/invite/email/', views.invite_to_event_through_email, name='invite users to event'),

    # Link/QR invitation handling
    path('event/invite/generate/<str:event_id>/', views.generate_invite_link, name='generate_invite_link'), 
    path('event/join/<str:event_id>/', views.join_via_link, name='join_via_link'),

    # Handle invitation acceptance/decline
    path('event/<str:event_id>/invitation/<str:action>/', views.handle_invitation, name='handle_invitation'),
]
