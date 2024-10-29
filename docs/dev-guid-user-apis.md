@ -0,0 +1,77 @@
# User Controller Guide

## Overview

The `UserController` in this application is a RESTful controller responsible for managing key user operations, focusing on authentication and user profile management. It provides a passwordless login experience by sending validation codes via email, allowing users to securely log in without passwords. Additionally, it supports functionalities to retrieve user information and update profiles, ensuring a streamlined user experience.

---

## 1. Sending a Validation Code

- **Endpoint**: `POST /api/user/request-code`
- **Purpose**: Generates a one-time validation code for the user and sends it to their email address. This code is used for passwordless login.

### Process
- Accepts an email address from the client.
- Generates a unique validation code.
- Stores the code temporarily in an in-memory storage (`validationCodes` map).
- Sends the validation code to the specified email via the `EmailService`.

### Response
- Returns a success message if the email is sent successfully.
- Returns an error message if there's an issue (e.g., invalid email or email sending failure).

---

## 2. Verifying the Validation Code

- **Endpoint**: `POST /api/user/verify-code`
- **Purpose**: Verifies the validation code provided by the user to log them in.

### Process
1. Accepts the email and validation code from the client.
2. Checks if the provided code matches the stored code in `validationCodes`.
3. If verified, removes the code from `validationCodes` and adds an active session for the user in `activeSessions`.
4. Creates a new user in the database if they do not already exist.

### Response
- Returns a success message if the code is valid.
- Returns an error message if the code is incorrect or expired.

---

## 3. Retrieving User Data

- **Endpoint**: `GET /api/user/get-data`
- **Purpose**: Retrieves specific user data fields or the full user profile if no fields are specified.

### Process
1. Validates the user's session by checking the `activeSessions` map for the user's email and code.
2. Fetches the user from the database using the email.
3. If no specific fields are requested, returns the complete user profile.
4. If specific fields are requested (e.g., email, addresses), returns only those fields.

### Response
- Returns the requested user data.
- Returns an error message if the session is invalid or the user is not found.

---

## 4. Updating User Profile

- **Endpoint**: `PATCH /api/user/update`
- **Purpose**: Updates specific details of the user’s profile.

### Process
1. Validates the session by checking the `activeSessions` map.
2. Ensures the email in the session matches the email in the request body.
3. Verifies and updates the user’s address list.
4. Saves the updated user profile in the database.

### Response
- Returns a success message if the profile is updated.
- Returns an error message if the session is invalid, the user is not found, or address validation fails.

---

This guide provides an overview of the `UserController`'s core operations, including authentication and profile management functionalities, to facilitate efficient and secure user interactions within the application.