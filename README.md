# Secret Santa Wishlist

A small wishlist application with a Spring Boot backend and an Angular frontend.
Wishlist owners can add and remove wishes, but reservation information is never
included in owner API responses. People opening the share link can see which
items are already taken and reserve an available item with their name and a note.

## Run locally

Requirements: Java 21+, Maven 3.9+, Node.js 20+ and npm.

```bash
# Terminal 1
cd backend
mvn spring-boot:run

# Terminal 2
cd frontend
npm install
npm start
```

Open <http://localhost:4200>. The API runs on <http://localhost:8080> and uses
an embedded H2 database stored in `backend/data/`.

## Important privacy behavior

- An owner key is generated when a list is created and stored only in that
  browser's local storage.
- Sharing uses a separate random token in the URL.
- Owner endpoints return item details without reservation fields.
- Guest endpoints expose reservation status, but reserved items cannot be
  reserved a second time.

This is an MVP link-based access model. For an internet-facing deployment,
replace owner keys with authenticated user accounts and serve everything over
HTTPS.
