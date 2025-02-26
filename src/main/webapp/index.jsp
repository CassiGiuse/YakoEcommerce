<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="it" data-bs-theme="dark">
  <head>
    <meta charset="utf-8" />
    <meta
      name="viewport"
      content="width=device-width, initial-scale=1, shrink-to-fit=no"
    />
    <title><%= application.getAttribute("APP_NAME") %></title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
      rel="stylesheet"
      integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
      crossorigin="anonymous"
    />
    <%@ include file="templates/linkFavicon.jsp" %>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@sweetalert2/theme-dark@5/dark.css" />
  </head>

  <body>
    <header><%@ include file="templates/navbar.jsp" %></header>

    <main
      class="d-flex justify-content-center align-items-center min-vh-100 p-2"
    >
      <section class="w-50">
        <h1 id="formTitle" class="mb-3">Login</h1>
        <form class="row g-3 needs-validation" novalidate id="userForm">
          <!-- Login Section -->
          <div class="col">
            <label for="usernameInput" class="form-label">Username</label>
            <div class="input-group has-validation">
              <span class="input-group-text" id="usernamePrepend">@</span>
              <input
                type="text"
                class="form-control"
                id="usernameInput"
                aria-describedby="usernamePrepend"
                required
                name="username"
                placeholder="Username"
                autocomplete="username"
              />
              <div class="invalid-feedback">Username non valido</div>
              <div class="valid-feedback">Perfetto!</div>
            </div>
          </div>
          <div class="col">
            <label for="passwordInput" class="form-label">Password</label>
            <div class="input-group has-validation">
              <input
                type="password"
                class="form-control"
                id="passwordInput"
                required
                placeholder="**********"
                autocomplete="current-password"
                name="passwd"
              />
              <div class="invalid-feedback">
                La password non rispetta i requisiti di sicurezza.
              </div>
              <div class="valid-feedback">Perfetto!</div>
            </div>
          </div>

          <!-- Signup section -->
          <div id="additionalDetails" class="mt-3" style="display: none">
            <div class="row g-3">
              <div class="col">
                <label for="firstNameInput" class="form-label">Nome</label>
                <input
                  type="text"
                  class="form-control"
                  id="firstNameInput"
                  placeholder="Nome"
                  name="nome"
                />
              </div>
              <div class="col">
                <label for="lastNameInput" class="form-label">Cognome</label>
                <input
                  type="text"
                  class="form-control"
                  id="lastNameInput"
                  placeholder="Cognome"
                  name="cognome"
                />
              </div>
            </div>
            <div class="row g-3 mt-1">
              <div class="col">
                <label for="telInput" class="form-label">Telefono</label>
                <div class="input-group has-validation">
                  <span class="input-group-text" id="suffissoTel">+39</span>
                  <input
                    type="tel"
                    pattern="[0-9]{3}-?[0-9]{3}-?[0-9]{4}"
                    class="form-control"
                    id="telInput"
                    aria-describedby="suffissoTel"
                    minlength="10"
                    maxlength="13"
                    placeholder="123-456-7890"
                    name="telefono"
                  />
                  <div class="invalid-feedback">
                    Numero di telefono non valido
                  </div>
                </div>
              </div>
              <div class="col">
                <label for="emailInput" class="form-label">Email</label>
                <div class="input-group has-validation">
                  <input
                    type="email"
                    class="form-control"
                    id="emailInput"
                    placeholder="example@example.com"
                    name="email"
                  />
                  <div class="invalid-feedback">Email non valida</div>
                </div>
              </div>
            </div>
          </div>
          <!-- Terms and Conditions -->
          <div class="row mt-3">
            <div class="col-12">
              <div class="form-check">
                <input
                  class="form-check-input"
                  type="checkbox"
                  id="invalidCheck"
                  required
                  name="trustIssue"
                />
                <label class="form-check-label" for="invalidCheck">
                  Accetto i <a href="#">Termini&Condizioni</a>
                </label>
                <div class="invalid-feedback">
                  È obbligatorio accettare le nostre condizioni
                </div>
              </div>
            </div>
            <div class="col-12">
              <div class="form-check">
                <input
                  type="checkbox"
                  id="toggleDetailsButton"
                  class="form-check-input"
                  name="isRegistration"
                />
                <label class="form-check-label" for="toggleDetailsButton">
                  Registrati
                </label>
              </div>
            </div>
          </div>

          <!-- Submit Button -->
          <div class="col-12">
            <button class="btn btn-primary" type="submit">Prosegui</button>
          </div>
        </form>
      </section>
    </main>

    <footer><%@ include file="templates/footer.jsp" %></footer>

    <script src="js/main.js"></script>
    <script
      src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
      integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r"
      crossorigin="anonymous"
    ></script>
    <script
      src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.min.js"
      integrity="sha384-BBtl+eGJRgqQAUMxJ7pMwbEyER4l1g+O15P+16Ep7Q9Q+zqX6gSbd85u4mG4QzX+"
      crossorigin="anonymous"
    ></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.js"></script>
  </body>
</html>
