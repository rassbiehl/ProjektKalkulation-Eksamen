# Contributing to ProjectKalkulation-Eksamen (AlphaManager)


Thank you for participating in our project.
___

## Before you start:
- Make sure that you are working on the latest 'main' branch (`git pull origin main`)
- Familiarize yourself with the project goals and existing code ([README.md](./README.md))
- Make sure to commit your changes in a new branch (not directly to the main-branch)
- When commiting your work make sure to describe your changes in a meaningful way
- Passwords are encrypted. To test user login locally, generate a hashed password using `BCryptPasswordEncoder`, then insert the admin user into your SQL database manually.

___

## How to contribute:
1. Create a new feature branch (`git checkout -b feature/my-feature`)
2. Run and test the project. (`mvn clean install`)
3. Write tests for any new features.
4. Open a pull request and describe your changes and new functionality

___

## Coding rules:
- Follow standard Java conventions (camelCase, PascalCase)
- Use previously written code as inspiration. Try to design your code in the same way, making our code more consistent
- Comment when writing complex code for better understanding
- You must test new features before you open a pull request
- Ensure Single **Responsibility Principle (SRP)**

___

# Tools
- Java 21
- Maven – Build tool
- Spring Boot – Backend framework
- Spring Security – Authentication & encryption (e.g. BCryptPasswordEncoder)
- Spring JDBC – Database access using JdbcTemplate
- SQL – Relational database
- H2 – In-memory test database
- Thymeleaf – Frontend templates
- Spring MVC – Model-View-Controller architecture
- JUnit 5 + Mockito – Unit testing & mocking
- Global Exception Handling – via @ControllerAdvice
- GitHub Actions – Continuous Integration (CI)
- IntelliJ IDEA – Recommended IDE
- Azure App Service – Cloud hosting
- Azure SQL Database – Production database


___

### Final note - No Brown M&Ms 🎸

To prove that you've read these guidelines, please include the phrase:

> 'No brown M&Ms'

...in your first pull request description.
This shows that you haven't skipped through this important document.

___ 

If you have any questions contact the maintainer

