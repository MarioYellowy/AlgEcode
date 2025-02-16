document.addEventListener("DOMContentLoaded", function() {
    const showDetailsBtn = document.getElementById("show-details-btn");
    const modal = document.getElementById("details-modal");
    const closeBtn = document.querySelector(".close-btn");
    const determinanteValue = document.getElementById("determinante-value");
    const matrizFinal = document.getElementById("matriz-final");
  
    showDetailsBtn.addEventListener("click", function() {
      fetch("http://localhost:8080/api/detalles", {
        method: "GET",
        headers: {
          "Content-Type": "application/json"
        }
      })
        .then(response => {
          if (!response.ok) {
            throw new Error("Error en la respuesta del servidor");
          }
          return response.json();
        })
        .then(data => {
          determinanteValue.textContent = data.determinante;
  
          const matrizTexto = data.matriz.map(fila => fila.join(" ")).join("\n");
          matrizFinal.textContent = matrizTexto;
  
          modal.style.display = "block";
        })
        .catch(error => {
          console.error("Error al obtener detalles:", error);
          alert("Error al obtener los detalles. Intenta de nuevo más tarde.");
        });
    });
  
    closeBtn.addEventListener("click", function() {
      modal.style.display = "none";
    });
  
    window.addEventListener("click", function(event) {
      if (event.target === modal) {
        modal.style.display = "none";
      }
    });
  });
  