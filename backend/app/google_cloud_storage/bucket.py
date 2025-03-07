from google.cloud import storage
import os

from google.cloud import storage

"""
image/jpeg
image/png
"""

def upload_to_gcs(bucket_name:str, file_bytes: bytes, destination_blob_name:str, content_type:str="image/jpeg"):
    """Uploads a byte array to the Google Cloud Storage bucket."""
    # Initialize a client
    client = storage.Client()

    # Get the bucket
    bucket = client.bucket(bucket_name)

    # Create a new blob (object) in the bucket
    blob = bucket.blob(destination_blob_name)

    # Upload the byte array
    blob.upload_from_string(file_bytes, content_type=content_type)

def download_from_gcs(bucket_name, source_blob_name):
    """Downloads a file from Google Cloud Storage as a byte array."""
    # Initialize a client
    client = storage.Client()

    # Get the bucket
    bucket = client.bucket(bucket_name)

    # Get the blob (object) from the bucket
    blob = bucket.blob(source_blob_name)

    # Download the file as bytes
    file_bytes = blob.download_as_bytes()

    return file_bytes  # Returns the content as bytes

def delete_from_gcs(bucket_name, source_blob_name):
    """Delete a file from Google Cloud Storage"""
    try:
        client = storage.Client()
        bucket = client.bucket(bucket_name)
        blob = bucket.blob(source_blob_name)

        generation_match_precondition = None
        blob.reload()
        generation_match_precondition = blob.generation

        blob.delete(if_generation_match=generation_match_precondition)
        
        return True
    except:
        return False
