# Frontend Architecture & Backend Connection Guide

This document explains the architecture of the React frontend (`elearning-frontend`) and details exactly how it establishes its connection to the Java Spring Boot backend.

## 1. Frontend Structure Overview
The application is built using **React** with the **Vite** bundler for fast development. 

### Key Directories in `/src`:
- **`/api`**: Contains all the logic for making HTTP requests to the backend. It isolates the data fetching logic from the UI components.
- **`/components`**: Reusable UI elements split by domain:
  - `/common`: Buttons, inputs, headers, loaders, etc.
  - `/course`: Course cards, video players, rating stars.
  - `/search`: Search bars and filter sidebars.
- **`/pages`**: Full-page views separated by roles:
  - `/auth`: Login and Registration pages.
  - `/student`: Student dashboard, enrolled courses list, quiz taking views, and strict chapter-by-chapter progression interfaces.
  - `/teacher`: Instructor dashboard, course creation forms (multi-chapter), analytic views.
- **`/context`**: React Context API providers (`AuthContext.jsx`) to manage global state like whether a user is currently logged in.

---

## 2. How the Frontend Connects to the Backend

The frontend communicates with the backend exclusively via **REST APIs** over HTTP. It uses **Axios**, a robust Javascript HTTP client, to facilitate this.

### `api/axiosConfig.js` - The Core Bridge
This is the most critical file for the connection. Here is how it functions:

1. **Base URL Setup**:
   It creates a global Axios instance pointing directly to the Spring Boot server port:
   ```javascript
   const API_BASE_URL = 'http://localhost:8081/api';
   const api = axios.create({ baseURL: API_BASE_URL });
   ```
   *Note: When deploying to production, this `localhost:8081` is replaced with the actual hosted backend URL.*

2. **Request Interceptor (Sending the JWT)**:
   Every time a component tries to fetch data, Axios intercepts the request right before it leaves the browser. It grabs the JWT out of `localStorage` and attaches it to the HTTP Headers:
   ```javascript
   config.headers.Authorization = `Bearer ${token}`
   ```
   This is how the Java backend knows *who* is making the request without needing session cookies.

3. **Response Interceptor (Handling Logouts)**:
   When the backend sends a response back to React, Axios intercepts it. If the backend replies with a `401 Unauthorized` (meaning the token expired or is invalid), the interceptor forcefully deletes the local token and redirects the user to the `/login` page automatically.

### Modular API Files
Instead of writing raw `axios.get('...')` inside React components, the app uses modular API files for clean separation:
- `authApi.js`: Contains `login(credentials)` and `register(data)`.
- `courseApi.js`: Contains `getAllCourses()`, `getCourseById(id)`, etc.
- `userApi.js`: Contains `getProfile()`.

**Example Flow:**
1. User clicks "Login" on the React page.
2. React calls `authApi.login({email, password})`.
3. `authApi` uses the `axiosConfig` to send a POST request to `localhost:8081/api/auth/login`.
4. Spring Boot verifies credentials and returns a JSON Web Token.
5. React saves the token to `localStorage` and updates `AuthContext` to log the user in.
