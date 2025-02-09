Create venv:
python -m venv venv

To run backend locally:
create .env (environment variables)
create concrete-spider-449820-p0-b07324f24234.json  (for GCS)

run dockerized backend:
docker-compose down             docker compose down 
docker-compose up --build       docker compose up --build

migration:
docker-compose exec web python manage.py makemigrations myapp
docker-compose exec web python manage.py migrate