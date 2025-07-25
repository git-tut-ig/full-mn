curl -i -d '{"name": "My book", "availability": "available"}' \
  -H 'Content-Type: application/json' -X POST localhost:8080/books/add

curl -i "localhost:8080/books/search"

curl -i "localhost:8080/authors/search"