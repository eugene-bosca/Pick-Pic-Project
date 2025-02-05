To run backend locally:
create .env

run dockerized backend:
docker-compose down
docker-compose up --build

migration:
docker-compose exec web python manage.py makemigrations myapp
docker-compose exec web python manage.py migrate