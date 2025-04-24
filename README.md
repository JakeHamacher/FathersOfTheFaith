# ChurchFatherArchive Web Application

## Project Overview
The **ChurchFatherArchive** web application is a project designed to provide an online archive of documents written by the Church Fathers. The application allows users to search, sort, and filter documents based on various criteria, including author, scripture references, and century. It was developed as a web application using **Vaadin** for the user interface and **Maven** for project management and dependency management.

The application is designed to be deployed using **Docker** for containerization and hosted on **Google Cloud** for scalability and reliability.

## Key Technologies Used
- **Vaadin**: A popular Java framework for building modern web applications with a rich user interface. Vaadin was used for both the frontend and backend of this application.
- **Maven**: A build automation tool used to manage project dependencies, build lifecycle, and deployment tasks.
- **Docker**: Containerization technology used to package the application, ensuring it runs consistently across different environments.
- **Google Cloud**: A cloud platform used to deploy the application, making it accessible online.

## Features
- **Search Functionality**: Allows users to search for documents based on keywords, including author names, scripture references, and more.
- **Sorting and Filtering**: Users can sort documents by author, document name, or century, and filter by scripture references and authors.
- **Web-Based Interface**: Built with Vaadin, providing an intuitive and user-friendly interface for interacting with the document archive.

## How to Access the Web Application
The ChurchFatherArchive web application is deployed and accessible at the following URL:
[fathersofthefaith.org](https://fathersofthefaith.org/)

Simply navigate to the link in your web browser to begin using the application.

Alternatively to run it locally,

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).
This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. The file can be found in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/myapp-1.0-SNAPSHOT.jar`

## Contributing

This project was created for academic purposes, but contributions are welcome! If you would like to contribute to improving the ChurchFatherArchive application, please follow these steps:

1. Fork the repository.
2. Create a new branch.
3. Commit your changes.
4. Push your changes to your forked repository.
5. Open a pull request to merge your changes.

## Acknowledgements

- **Nave Security**: For conducting external penetration testing and stress testing of the application to ensure its security and reliability.
- **CCEL.org**: For providing the downloadable files of Church Father writings
