document.addEventListener("DOMContentLoaded", function() {
  
    let mode = ""; 
    let matrixOption = "";
  
  
    const encodeBtn = document.getElementById("codificar-btn");
    const decodeBtn = document.getElementById("decodificar-btn");
    const matrixOptionButtons = document.querySelectorAll(
      ".body-container__conversion-options-btn__option1, .body-container__conversion-options-btn__option2, .body-container__conversion-options-btn__option3"
    );
    const actionButton = document.querySelector(".body-container__conversion-options-btn__code");
    const inputTextArea = document.querySelector(".body-container__conversion-input-txt");
    const outputTextArea = document.querySelector(".body-container__conversion-output-txt");
  
    encodeBtn.addEventListener("click", function() {
      mode = "encode";
      encodeBtn.classList.add("active");
      decodeBtn.classList.remove("active");
      
      actionButton.textContent = "Codificar";
    });
  
    decodeBtn.addEventListener("click", function() {
      mode = "decode";
      decodeBtn.classList.add("active");
      encodeBtn.classList.remove("active");
      actionButton.textContent = "Decodificar";
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
      if (mode === "" || matrixOption === "" || textInput === "") {
        alert("Por favor, selecciona el modo, la opción de matriz y escribe el texto.");
        return;
      }
  
      const payload = {
        mode: mode,
        text: textInput,
        matrix: parseInt(matrixOption, 10) 
      };
  
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