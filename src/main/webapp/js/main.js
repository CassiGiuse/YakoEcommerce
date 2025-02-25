"use strict";

function prepareData() {
  const form = document.getElementById("userForm");
  const formData = new FormData(form);

  const isRegistrationChecked = document.getElementById(
    "toggleDetailsButton"
  ).checked;
  if (!isRegistrationChecked) {
    formData.set("isRegistration", "off");
  }

  return Object.fromEntries(formData.entries());
}

async function registerUser() {
  const data = prepareData();

  try {
    const response = await fetch("/servlet_tre/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      throw new Error(`Errore HTTP! Status: ${response.status}`);
    }

    const result = await response.json();
    console.log("Risposta dal server:", result);
  } catch (error) {
    console.error("Errore durante l'invio dei dati:", error.message);
  }
}

function toggleDetailsPane() {
  const details = document.getElementById("additionalDetails");
  const inputs = Array.from(details.querySelectorAll("input"));
  const formTitle = document.getElementById("formTitle");

  if (details.style.display === "none") {
    details.style.display = "block";
    formTitle.innerText = "Registrati";
    inputs.forEach((input) => {
      input.setAttribute("required", "");
    });
  } else {
    formTitle.innerText = "Login";
    details.style.display = "none";
    inputs.forEach((input) => {
      input.removeAttribute("required");
    });
  }
}

const main = () => {
  const forms = document.querySelectorAll(".needs-validation");

  document
    .getElementById("toggleDetailsButton")
    .addEventListener("click", toggleDetailsPane);

  Array.from(forms).forEach((form) => {
    form.addEventListener(
      "submit",
      (event) => {
        event.preventDefault();

        if (!form.checkValidity()) {
          event.stopPropagation();
        } else {
          registerUser();
        }

        form.classList.add("was-validated");
      },
      false
    );
  });
};

window.addEventListener("DOMContentLoaded", main);
