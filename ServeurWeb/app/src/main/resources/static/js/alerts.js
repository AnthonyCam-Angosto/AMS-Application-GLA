/**
 * Scripts pour la page alertes : gestion basique du formulaire.
 * À compléter avec validation et envoi asynchrone si nécessaire.
 */
document.addEventListener('DOMContentLoaded', function(){
  const form = document.querySelector('form[th\:object]') || document.querySelector('form');
  if(form){
    form.addEventListener('submit', function(){
      // comportement futur : validation et envoi asynchrone
      console.log('Soumission du formulaire d\'alerte');
    });
  }
});
