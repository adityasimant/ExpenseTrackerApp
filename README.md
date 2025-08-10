# Smart Daily Expense Tracker – Jetpack Compose (MVVM)

<img width="1842" height="1177" alt="Frame 34" src="https://github.com/user-attachments/assets/c49ac130-c319-4f7a-8173-05ba21d210a4" />


The Smart Daily Expense Tracker is a multi-screen app designed for small business owners to easily capture, view, analyze, and export their daily expenses. It addresses the common problem of expenses being lost on WhatsApp chats or paper, helping users maintain accurate cash flow records.
The app is built with Jetpack Compose and follows a clean MVVM architecture for scalability and maintainability.

## Use of AI
I used AI tools like ChatGPT and GitHub Copilot to determine the optimal MVVM architecture for the module. AI also helped me optimize LazyColumn implementations for smooth scrolling and performance. I leveraged AI to write efficient SQL queries for Room DAO classes and to set up Navigation Compose for seamless screen transitions.


## Technical Implementation

- Architecture: MVVM (Clean Architecture) + Repository pattern
- UI: Jetpack Compose with Material 3 components
- State Management: ViewModel, StateFlow, Channels and Flows
- Navigation: Jetpack Navigation Compose
- Data Storage with Room database
- Dependency Injection with Hilt (Dagger)

## Prompt Logs
1. Determining Architecture Setup:
   ```
    I am tasked with making a module for google, I have to determine the architecture based on usecases. Help me determining a clean MVVM architecture for the application with the following usecases according to the best practices that match the googles code quality and sclability. We have to use Navigation compose for navigations and have to use nested graphs. We will setup DI modules with Hilt. Now with this context we need to layer the following the SOLID principle and seperating layers based on their usecases. Note that this app will be scaled later and will have lot of configs for UI and data. Feel free to ask clarifying questions before proceeding.

2. Optimizing LazyColumn Performance
   ```
   My LazyColumn in Jetpack Compose is laggy when displaying many expense items. How can I optimize it for better scrolling performance?

3. Writing SQL Queries for DAOs
   ```
   We have cases of fetching reports of last 7 days or based on date ranges. We might also need to group and sort data later. Help me define a DAO interface for this entity so that I can complete all the usecases

4. Validation Stragergy
   ```
   I have a screen with many inputs, how can I validate inputs with single data UI state. Also whats the best practises to validate these data with ViewModels.
   
##  Implemented Features

- [x] **Expense Entry** – Add expenses with title, amount, category, optional notes, and receipt image for better record-keeping.
- [x] **Expense Reports** – Review your last 7 days of spending with insightful metrics, category-wise breakdowns, and bar/line chart visualizations.
- [x] **Date Filtering** – Easily switch between today's expenses and previous dates using a calendar or filter option.
- [x] **View Modes** – Toggle views to group expenses by time or by category for clearer analysis.
- [x] **Charts (Mocked)** – Interactive bar or line charts for a visual overview of your spending patterns.
- [x] **Export (Simulated)** – Generate mock PDF/CSV reports and simulate sharing for quick access to your data.

## 📎 Useful Links

- [**Download APK**](https://github.com/adityasimant/ExpenseTrackerApp/releases/download/v1/ExpenseTracker.apk)
- [**Source Code (ZIP)**](https://github.com/adityasimant/ExpenseTrackerApp/archive/refs/tags/v1.zip)








