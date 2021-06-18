var sceltaData;
window.onload = function () {
	/* Setto come data minima da poter selezionare quella di oggi */
	sceltaData = document.getElementById("sceltaData");
	const oggi = new Date();
	const year = oggi.getFullYear();
	const month = Number(oggi.getMonth()) + 1;
	const date = oggi.getDate();
	sceltaData.min = `${year}-${month < 10 ? "0" + String(month) : month}-${date < 10 ? "0" + String(date) : date
		}`;

	/* Intercetto il submit del form e lo rederigo come una richiesta di fetch dati dal server */
	const form = document.querySelector("form");
	const processForm = function processForm(e) {
		if (e.preventDefault) e.preventDefault();
		console.log(sceltaData.value);
		// http://ablant.xyz:8080/safe-study-service/aule?data=yyyy-MM-gg

		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! MODIFICA 1 RUL
		fetch(`http://is.ablant.xyz:8080/safe-study-service/aule?data=${sceltaData.value}`, {
			mode: "cors",
		})
			.then((res) => res.json())
			.then((data) => {
				// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! SORTING delle aule
				data.sort((a, b) => a.aula.nomeAula < b.aula.nomeAula ? -1 : 1);
				console.log(data);
				showTables(data);
			})
			.catch((e) => alert("Oops, qualcosa è andato storto :("));

		// Return false to prevent the default form behavior
		return false;
	};
	if (form.attachEvent) {
		form.attachEvent("submit", processForm);
	} else {
		form.addEventListener("submit", processForm);
	}
};

const fasceOrarieMattina = ["9", "10", "11", "12"];
const fasceOrariePomeriggio = ["14", "15", "16", "17"];

/**
 * Funzione che mi nasconde il form e mi genera la tabella con tutte le disponibilità appena ottenute
 * @param {Array} data
 */
function showTables(data) { // data inteso come dati ricevuti dal server
	//Mostro le tabelle
	document.querySelector("#mattina").classList.remove("hidden");
	document.querySelector("#pomeriggio").classList.remove("hidden");

	// genero prima le righe della mattina
	var tbodyMattina = document.querySelector("#mattina tbody");
	console.log(tbodyMattina);
	tbodyMattina.innerHTML = "";

	data.forEach(function (ele) {
		var row = document.createElement("tr");
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! id -> nomeAula; aria_condizionata -> ariaCondizionata; capianza -> numeroPosti
		row.innerHTML = `<tr><th scope="row">\
      		${ele.aula.id || ele.aula.nomeAula}\
      		<span class="tip">\
      		Capienza massima: ${ele.aula.capienza || ele.aula.numeroPosti}<br>\
      		Edificio: ${ele.aula.edificio}<br>\
      		Piano: ${ele.aula.piano}<br>\
      		Aria condizionata: ${(ele.aula.aria_condizionata || ele.aula.ariaCondizionata) ? "Si" : "No"}<br>\
      		</span>\
      		</th>`;

		for (let orario of fasceOrarieMattina) {
			let disp = ele.availabilities[orario];

			if (disp) {
				row.innerHTML = row.innerHTML + `<td onclick="prenota('${ele.aula.id || ele.aula.nomeAula}', ${orario})">${disp}</td>`;
			} else {
				row.innerHTML = row.innerHTML + `<td class="table-danger" onclick="prenota('${ele.aula.id || ele.aula.nomeAula}', ${orario})">0</td>`;
			}
		}

		row.innerHTML = row.innerHTML + "</tr>";
		tbodyMattina.appendChild(row);
	});

	// genero le righe della mattina
	var tbodyPomeriggio = document.querySelector("#pomeriggio tbody");
	tbodyPomeriggio.innerHTML = "";

	data.forEach(function (ele) {
		var row = document.createElement("tr");
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! id -> nomeAula; aria_condizionata -> ariaCondizionata; capianza -> numeroPosti
		row.innerHTML = `<tr><th scope="row">\
    		${ele.aula.id || ele.aula.nomeAula}\
    		<span class="tip">\
    		Capienza massima: ${ele.aula.capienza || ele.aula.numeroPosti}<br>\
    		Edificio: ${ele.aula.edificio}<br>\
    		Piano: ${ele.aula.piano}<br>\
    		Aria condizionata: ${(ele.aula.aria_condizionata || ele.aula.ariaCondizionata) ? "Si" : "No"}<br>\
    		</span>\
    		</th>`;

		for (let orario of fasceOrariePomeriggio) {
			let disp = ele.availabilities[orario];

			if (disp) {
				row.innerHTML = row.innerHTML + `<td onclick="prenota('${ele.aula.id || ele.aula.nomeAula}', ${orario})">${disp}</td>`;
			} else {
				row.innerHTML = row.innerHTML + `<td class="table-danger" onclick="prenota('${ele.aula.id || ele.aula.nomeAula}', ${orario})">0</td>`;
			}
		}

		row.innerHTML = row.innerHTML + "</tr>";
		tbodyPomeriggio.appendChild(row);
	});
}

/**
 * Funzione che data aula e ora si preoccupa di inviare una richiesta di prenotazione al server
 * @param {String} aula 
 * @param {String} ora 
 */
function prenota(aula, ora) {
	ora = ora < 10 ? "0" + ora : ora;
	let conferma = confirm(`Confermi di voler prenotare l'aula ${aula} alle ore ${ora}:00?`);
	if (conferma) {
		let mail = prompt("Inserisci la tua mail universitaria.");

		while (!validateEmail(mail)) {
			mail = prompt("Prego inserire una mail universitaria corretta.");
		}

		let url = `http://is.ablant.xyz:8080/safe-study-service/prenotazioni?aulaid=${aula}&giornoOra=${sceltaData.value + "-" + ora}&email=${mail.toLowerCase()}`;
		console.log(url);
		location.href = url;
	}
}

/**
 * Funzione che verifica se una stringa corrisponde a una mail universitaria di torvergata
 * @param {String} email 
 * @returns {boolean} email is valid
 */
function validateEmail(email) {
	const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@students.uniroma2.eu/;
	return re.test(String(email).toLowerCase());
}
