# Ticket Management System

## Overview
This is a **web-based ticket management system** designed to help users create, manage, track, and organize tickets efficiently. The system provides **various functionalities** including ticket creation, milestone tracking, dynamic status updates, tag-based filtering, and a collaborative commenting system.

---

## Features

### **🎫 Ticket Management**
- Users can **create, view, edit, and delete tickets**.
- In the **Tickets View**, all tickets are displayed as cards, each containing:
  - A **view button** to see the details.
  - An **edit button** to modify the ticket.
  - A **quick edit function** (for dynamically changing the title and description).
  - A **change state button** to cycle the ticket status between **Open → In Progress → Done**.
  - **Closing a ticket permanently** requires direct editing.
- Users can **assign an estimated resolution time** during creation.
- **Assigned users** see an additional button to update the **time spent** on resolving the ticket.

### **📊 Board View**
- A **Kanban-style board** displays tickets categorized by **status**: Open, In Progress, Done, and Closed.
- A **Summary column** provides:
  - The **total number of tickets** per status.
  - A **progress bar** displaying the ratio of **time spent vs estimated time**.
- Clicking on a ticket opens a **detailed summary popup**.

### **🔔 Watched Tickets**
- Users can **flag a ticket as watched** in the **ticket details page**.
- The sidebar dynamically updates with the **count of watched tickets**.
- Clicking on "X Watched" in the sidebar leads to a **summary page** displaying all watched tickets.

### **📅 Milestone Management**
- Users can **create milestones** via the sidebar.
- Each milestone can **contain multiple tickets**.
- Tickets can be added to a milestone via the **dedicated milestone screen**.
- **Milestones are displayed as cards**, with a **progress bar** indicating the ratio of **total vs resolved tickets**.
- Clicking the **"Mark as Completed"** button **closes** the milestone.

### **🏷️ Tagging System**
- Users can **add tags** to tickets in the **ticket details page**.
- Tags are displayed in the **left sidebar under "Tags"**.
- **Duplicate tags are not allowed**.
- Clicking on a tag filters and displays **only the tickets associated with that tag**.

### **💬 Ticket Comments**
- Users can **leave comments** on tickets via the **ticket details page**.
- Each user can **delete their own comments**.
- Users can **reply to other comments**.
- **If a user deletes a comment that has replies, all replies are also deleted**.

---
## **🔧 Technologies Used**
- **Frontend:** Thymeleaf, Bootstrap
- **Backend:** Spring Boot, Spring MVC
- **Database:** Hibernate, JPA
- **Authentication:** Spring Security
- **Version Control:** Git, GitHub

---
## **📌 How to Run the Project**
1. Clone the repository:
   ```bash
   git clone git@github.com:MattiaVerdolin/ticket-service.git
   ```
2. Navigate to the project directory:
   ```bash
   cd ticket-service
   ```
3. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Open **`http://localhost:8080`** in your browser.

---
## **👤 Author**
**Mattia Verdolin** [📧mattia.verdolin@student.supsi.ch](mailto:mattia.verdolin@student.supsi.ch)

