<%@ page import="java.time.Year" %>

<footer class="bg-body-tertiary text-center text-lg-start">
  <div class="text-center p-3" style="background-color: rgba(0, 0, 0, 0.05)">
    <%= "\u00A9 " + Year.now().getValue() %> Copyright:
    <a class="text-body" href="https://github.com/CassiGiuse">CassiGiuse</a>
  </div>
</footer>

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