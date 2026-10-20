# FinTrack — quick test checklist

## Before demo
- [ ] JDK 17: `java -version`
- [ ] Maven: `mvn -version`
- [ ] Run `database/schema.sql` in MySQL Workbench.
- [ ] Configure `FINTRACK_DB_URL`, `FINTRACK_DB_USER`, `FINTRACK_DB_PASSWORD` for the process that starts Tomcat.
- [ ] Build: `mvn clean package`
- [ ] Deploy `target/FinTrack.war` to Tomcat 10.1 `webapps`.
- [ ] Open `http://localhost:8080/FinTrack/`.

## Functional checks
1. Register a new email with an 8+ character password.
2. Sign in with that account; incorrect password should fail.
3. Add one income and one expense; confirm dashboard totals and chart.
4. Open Budgets and create a category/month budget; verify spent amount after matching expense.
5. Open Savings goals and create/delete a goal.
6. Create a second user and confirm their transactions are not visible to the first user.
7. Sign out and confirm dashboard redirects to login.

## Known scope
- Transaction creation/listing and deletion are implemented; transaction editing is not yet implemented.
- Protect production deployment with CSRF tokens, stricter input/rate limiting, secure-cookie settings behind HTTPS, and automated tests.
- External Bootstrap/Chart.js/Google Fonts assets require internet access in the browser.
