# Pick-Pic

- [Pick-Pic](#pick-pic)
    - [Overview](#overview)
- [Architecture](#architecture)
- [Usage](#usage)

# Overview

This is source code for the backend server of Project Pick-Pic.

## Technology used

* Docker Container
* Django
* Django Rest Framework
* Google Cloud Platform (Google Cloud Storage)
* MySQL

# Architecture

### Database
The database used is MySQL, containerized with Docker. The database is managed (including migrations) with Django framework.

### RESTful Web service
The backend is built using Django Rest Framework, with Swagger UI at `/api/docs`.

### File System/Storage
The backend uses Google Cloud Storage as the file storage system.

### Containerization
Both the database and the Django REST Service are containerized through Docker containers.

## Source Code Navigation (Django)

`./app/views.py` is where you define the functions for custom endpoints

`./app/urls.py` is where you define the endpoints

`./app/models.py` is where you define the database models

`./app/serializers.py` is where you define the model serialization

`./app/tests.py` is where you define the unit tests

`./app/migrations` is the folder that contains all migration .py files **(DO NOT MODIFY OR DELETE)**

`./app/google_cloud_storage` contains functions that access google_cloud_storage

# Usage
### To run backend locally
* Create .env (environment variables)
* Create concrete-spider-449820-p0-b07324f24234.json (for GCS)

### Start dockerized backend
1) `docker-compose down` or `docker compose down` 
2) `docker-compose up --build` or `docker compose up --build`

### (Local only) run Cloud SQL proxy
1) `chmod +x cloud-sql-proxy`
2) `./cloud-sql-proxy --credentials-file=GOOGLE_APPLICATION_CREDENTIALS CLOUDSQL_CONNECTION_NAME`
    - note: see .env for CLOUDSQL_CONNECTION_NAME


### Manual migration
* Create migration: `docker-compose exec web python manage.py makemigrations app`
* Apply migration: `docker-compose exec web python manage.py migrate`

### Package management
* python pip is used to manage all packages used, all packages that are required are detailed in requirements.txt
* to install all packages through pip, run `pip install -r requirements`