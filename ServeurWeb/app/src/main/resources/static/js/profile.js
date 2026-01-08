/**
 * Ouvre une modal HTML (affiche l'élément via `display`).
 * @param {string} id - Id de l'élément modal
 */
function openModal(id) {
  document.getElementById(id).style.display = "block";
}

/**
 * Ferme une modal HTML.
 * @param {string} id - Id de l'élément modal
 */
function closeModal(id) {
  document.getElementById(id).style.display = "none";
}

// Fermer si clic en dehors: ferme toute modal si le clic cible l'overlay
window.onclick = function(event) {
  const modals = document.getElementsByClassName("modal");
  for (let modal of modals) {
    if (event.target === modal) {
      modal.style.display = "none";
    }
  }
}
