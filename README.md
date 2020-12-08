# Aistia

### Purpose
 The application is a web-scrapper agent that searches at regular intervals, in a real estate website for new posts of interest. Posts that have not been encountered before, are sent through email to those interested. For example, you are in search of home to rent and you have no time to search all those websites and every time something comes up, you realize it belatedly, losing the opportunity.


### Implementation
- *Azure Functions* are used for the regular execution of the agent.
- Gmail SMTP is used for the mail communication of the results.
- An Azure SQL database has been used to store the already-encountered posts

### Disclaimer
The application has been implemented for programming practice. It is not intended to be used as a means to bypass offered paid services of real estate websites, that serve the same purpose.