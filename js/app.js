document.addEventListener("DOMContentLoaded", function() {
  let mode = ""; 
  let matrixOption = ""; 

  const encodeBtn = document.getElementById("codificar-btn");
  const decodeBtn = document.getElementById("decodificar-btn");
  const matrixOptionButtons = document.querySelectorAll(".body-container__conversion-options-btn__option1, .body-container__conversion-options-btn__option2, .body-container__conversion-options-btn__option3");
  const actionButton = document.querySelector(".body-container__conversion-options-btn__code");
  const inputTextArea = document.querySelector(".body-container__conversion-input-txt");
  const outputTextArea = document.querySelector(".body-container__conversion-output-txt");

  const matrixOptionsContainer = document.getElementById("matrix-options");
  const keyMatrixInput = document.getElementById("key-matrix-input");

  encodeBtn.addEventListener("click", function() {
    mode = "encode";  
    encodeBtn.classList.add("active");
    decodeBtn.classList.remove("active");
    actionButton.textContent = "Codificar";
    
    matrixOptionsContainer.style.display = "block";
    keyMatrixInput.style.display = "none";
  });

  decodeBtn.addEventListener("click", function() {
    mode = "decode";
    decodeBtn.classList.add("active");
    encodeBtn.classList.remove("active");
    actionButton.textContent = "Decodificar";
    
    matrixOptionsContainer.style.display = "none";
    keyMatrixInput.style.display = "block";
  });

  matrixOptionButtons.forEach(function(button) {
    button.addEventListener("click", function() {
      matrixOptionButtons.forEach(btn => btn.classList.remove("active"));
      button.classList.add("active");
      matrixOption = button.getAttribute("data-option"); 
    });
  });

  actionButton.addEventListener("click", function() {
    const textInput = inputTextArea.value.trim();

    if (mode === "" || textInput === "") {
      alert("Por favor, selecciona el modo y escribe el texto.");
      return;
    }

    let payload = {
      mode: mode, 
      text: textInput,
    };

    if (mode === "encode" && matrixOption === "") {
      alert("Por favor, selecciona una opción de matriz.");
      return;
    }

    if (mode === "encode") {
      payload.matrix = parseInt(matrixOption, 10);  
    } else {
      try {
        const keyMatrix = JSON.parse(keyMatrixInput.querySelector("#matrix-txt").value); 
        payload.keyMatrix = keyMatrix;
      } catch (error) {
        alert("Por favor, ingresa una matriz de clave válida en formato JSON.");
        return;
      }
    }

    fetch("http://localhost:8080/api/convert", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    })
    .then(response => response.json())
    .then(data => {
      outputTextArea.value = data.result; 
    })
    .catch(error => {
      console.error("Error en la comunicación con el backend:", error);
    });
  });
});
