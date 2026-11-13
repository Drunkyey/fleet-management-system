<template>
  <v-container>
    <v-card class="pa-5 mt-5">
      <v-row align="center">
        <v-col cols="12" md="8">
          <v-card-title class="text-h4 px-0">Alocări Șoferi pe Vehicule</v-card-title>
        </v-col>
        <v-col cols="12" md="4" class="text-right">
          <v-btn color="success" @click="openCreateDialog">Adaugă Alocare</v-btn>
        </v-col>
      </v-row>
      
      <v-table class="mt-4">
        <thead>
          <tr>
            <th class="text-left">ID</th>
            <th class="text-left">Șofer</th>
            <th class="text-left">Vehicul (Nr. Înmatriculare)</th>
            <th class="text-left">Dată Preluare</th>
            <th class="text-left">Dată Predare</th>
            <th class="text-left">Observații</th>
            <th class="text-right">Acțiuni</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="assignment in assignments" :key="assignment.id">
            <td>{{ assignment.id }}</td>
            <td>
              <strong>{{ assignment.driver ? assignment.driver.firstName + ' ' + assignment.driver.lastName : 'N/A' }}</strong>
            </td>
            <td>
              <v-chip size="small" variant="flat" color="blue-lighten-4" style="color: black !important; font-weight: bold;">
                {{ assignment.vehicle ? assignment.vehicle.licensePlate : 'N/A' }}
              </v-chip>
            </td>
            <td>{{ assignment.assignDate }}</td>
            <td>
              <span v-if="assignment.returnDate">{{ assignment.returnDate }}</span>
              <v-chip v-else size="small" color="success" class="font-weight-bold">Activ</v-chip>
            </td>
            <td>{{ assignment.notes }}</td>
            <td class="text-right">
              <v-btn icon size="small" color="blue" class="me-2" @click="openEditDialog(assignment)">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
              <v-btn icon size="small" color="red" @click="deleteAssignment(assignment.id)">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </td>
          </tr>
          <tr v-if="assignments.length === 0">
            <td colspan="7" class="text-center text-grey">Nu există alocări înregistrate.</td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- Dialog Adăugare / Editare Alocare -->
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ isEditMode ? 'Editează Alocarea' : 'Adaugă Alocare Nouă' }}</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12" sm="6">
                <v-select
                  v-model="currentAssignment.driver"
                  :items="drivers"
                  item-title="fullName"
                  item-value="id"
                  label="Selectează Șoferul"
                  return-object
                  required
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-select
                  v-model="currentAssignment.vehicle"
                  :items="vehicles"
                  item-title="licensePlate"
                  item-value="id"
                  label="Selectează Vehiculul"
                  return-object
                  required
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentAssignment.assignDate" label="Dată Preluare (YYYY-MM-DD)" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentAssignment.returnDate" label="Dată Predare (Opțional)"></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field v-model="currentAssignment.notes" label="Observații (ex: Preluat cu plinul făcut)"></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="dialog = false">Anulează</v-btn>
          <v-btn color="success" variant="text" @click="saveAssignment">Salvează</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  name: 'AssignmentManager',
  data() {
    return {
      assignments: [],
      vehicles: [],
      drivers: [],
      dialog: false,
      isEditMode: false,
      currentAssignment: {
        id: null,
        driver: null,
        vehicle: null,
        assignDate: '',
        returnDate: '',
        notes: ''
      }
    }
  },
  mounted() {
    this.fetchAssignments();
    this.fetchVehicles();
    this.fetchDrivers();
  },
  methods: {
    fetchAssignments() {
      axios.get('http://localhost:8083/api/assignments')
        .then(response => { this.assignments = response.data; })
        .catch(error => { console.error('Eroare la preluarea alocărilor:', error); });
    },
    fetchVehicles() {
      axios.get('http://localhost:8083/api/vehicles')
        .then(response => { this.vehicles = response.data; })
        .catch(error => { console.error('Eroare la preluarea vehiculelor:', error); });
    },
    fetchDrivers() {
      axios.get('http://localhost:8083/api/drivers')
        .then(response => {
          // Creăm o proprietate 'fullName' pentru a o folosi în dropdown
          this.drivers = response.data.map(driver => ({
            ...driver,
            fullName: `${driver.firstName} ${driver.lastName}`
          }));
        })
        .catch(error => { console.error('Eroare la preluarea șoferilor:', error); });
    },
    openCreateDialog() {
      this.isEditMode = false;
      this.currentAssignment = { 
        id: null,
        driver: null,
        vehicle: null, 
        assignDate: new Date().toISOString().slice(0, 10), 
        returnDate: '', 
        notes: '' 
      };
      this.dialog = true;
    },
    openEditDialog(assignment) {
      this.isEditMode = true;
      this.currentAssignment = { ...assignment };
      this.dialog = true;
    },
    saveAssignment() {
      // Dacă Data Predare e goală, trimitem null pentru a nu crea erori de parsare pe backend
      if (this.currentAssignment.returnDate === '') {
        this.currentAssignment.returnDate = null;
      }
      
      if (this.isEditMode) {
        axios.put(`http://localhost:8083/api/assignments/${this.currentAssignment.id}`, this.currentAssignment)
          .then(() => {
            this.dialog = false;
            this.fetchAssignments();
          })
          .catch(error => console.error('Eroare la editare alocare:', error));
      } else {
        axios.post('http://localhost:8083/api/assignments', this.currentAssignment)
          .then(() => {
            this.dialog = false;
            this.fetchAssignments();
          })
          .catch(error => {
            // Dacă backend-ul ne trimite o eroare 400 (Bad Request), afișăm mesajul text
            if (error.response && error.response.status === 400) {
              alert(error.response.data);
            } else {
              console.error('Eroare la salvare alocare:', error);
            }
          });
      }
    },
    deleteAssignment(id) {
      if (confirm('Sigur doriți să ștergeți această alocare?')) {
        axios.delete(`http://localhost:8083/api/assignments/${id}`)
          .then(() => { this.fetchAssignments(); })
          .catch(err => console.error('Eroare la ștergere alocare:', err));
      }
    }
  }
}
</script>