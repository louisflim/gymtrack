# GymTrack Web App

GymTrack is a gym membership and attendance management system. This web app is a React frontend built with Vite and is designed to work with the Spring Boot backend in the `backend/` folder.

The app currently focuses on authentication, account onboarding, and a basic dashboard:

- Sign in for existing users
- Register new members or gym owners
- Send login and registration requests to the backend API
- Store the returned auth data in local storage after a successful login
- Show a dashboard after login or registration

## Tech Stack

- React 19
- Vite
- React Router
- Axios

## Project Structure

- `src/pages/login.jsx` - sign in screen
- `src/pages/register.jsx` - registration screen
- `src/pages/dashboard.jsx` - dashboard screen
- `src/api/auth.js` - API calls for login and registration
- `src/api/axiosInstance.js` - shared Axios client for backend requests
- `src/css/auth.css` - auth and dashboard styling

## Backend Connection

The frontend sends requests to the backend auth routes under `/api/auth`. During local development, Vite proxies those requests to `http://localhost:8080`.

## Running the App

From the `web/` folder:

```bash
npm install
npm start
```

You can also use `npm run build` to verify the production build.

## About the Project

GymTrack is intended to help a gym handle membership access, authentication, and the first step toward a broader management flow for staff and members. The backend provides the API layer, while this frontend provides the user-facing login and registration experience.
