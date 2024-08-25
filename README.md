# SIMPLE SERVER

## Overview

This server application is designed to work with the accompanying client application at [simple_client](https://github.com/JeffW12345/simple_client).

There is a demo of the apps [here](https://www.loom.com/share/c5dbbcf20a4c4d70aaa1f0efcdca2532).

This app was created using Java 17, but may work on other versions of Java.

### Features

1. **POST Requests**: Accepts objects representing customers via POST requests and stores those customers in a local SQLite database.
2. **GET Requests**: Provides JSON representations of customer objects via a GET endpoint.

### Main Classes

- **Customer**: A model class representing customer data. It also contains a method that provides a JSON representation 
of itself. 
- **CustomerController**: Handles routing for the endpoints.
- **CustomerService**: A service class that provides methods for posting and retrieving customer data, which are called 
by the controller.

## Instructions

### Running the Server

To start the application, run the `Main` method in your IDE or via the command line.

### Making Requests

#### POST Requests

You can create customer entries by sending POST requests with JSON data. This can be done using the accompanying client 
or other tools like Postman.

#### GET Requests

To retrieve a customer by their reference, send a GET request to the following endpoint:

http://localhost:8080/customers/search?customerReference=customer-reference

replacing customer-reference with the customer's reference (without quotation marks, even though it's a String). For 
example:

http://localhost:8080/customers/search?customerReference=004

This will return the JSON representation of the customer object with the specified reference.

### Database Access

The application uses a local SQLite database to store customer data. You can view the stored customers using an SQLite client. Connect to the database and execute the following SQL query:

```sql
SELECT * FROM customer;
