<template>
  <v-container>
    <v-card class="pa-5 mt-5">
      <v-row align="center">
        <v-col cols="12" md="8">
          <v-card-title class="text-h4 px-0">Management Șoferi</v-card-title>
        </v-col>
        <v-col cols="12" md="4" class="text-right">
          <v-btn color="success" @click="openCreateDialog">Adaugă Șofer</v-btn>
        </v-col>
      </v-row>
      
      <v-table class="mt-4">
        <thead>
          <tr>
            <th class="text-left">ID</th>
            <th class="text-left">Nume și Prenume</th>
            <th class="text-left">Email</th>
            <th class="text-left">Telefon</th>
            <th class="text-left">Nr. Permis</th>
            <th class="text-left">Data Angajării</th>
            <th class="text-right">Acțiuni</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="driver in drivers" :key="driver.id">
            <td>{{ driver.id }}</td>
            <td><strong>{{ driver.firstName }} {{ driver.lastName }}</strong></td>
            <td>{{ driver.email }}</td>
            <td>{{ driver.phone }}</td>
            <td>{{ driver.licenseNumber }}</td>
            <td>{{ driver.hireDate }}</td>
            <td class="text-right">
              <v-btn icon size="small" color="blue" class="me-2" @click="openEditDialog(driver)">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
              <v-btn icon size="small" color="red" @click="deleteDriver(driver.id)">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </td>
          </tr>
          <tr v-if="drivers.length === 0">
            <td colspan="7" class="text-center text-grey">Nu există șoferi înregistrați.</td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- Dialog Adăugare / Editare Șofer -->
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ isEditMode ? 'Editează Șoferul' : 'Adaugă Șofer Nou' }}</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentDriver.firstName" label="Prenume" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentDriver.lastName" label="Nume" required></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field v-model="currentDriver.email" label="Email" type="email" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentDriver.phone" label="Telefon" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentDriver.licenseNumber" label="Număr Permis" required></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field v-model="currentDriver.hireDate" label="Data Angajării (YYYY-MM-DD)" required></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="dialog = false">Anulează</v-btn>
          <v-btn color="success" variant="text" @click="saveDriver">Salvează</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  name: 'DriverManager',
  data() {
    return {
      drivers: [],
      dialog: false,
      isEditMode: false,
      currentDriver: {
        id: null,
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        licenseNumber: '',
        hireDate: ''
      }
    }
  },
  mounted() {
    this.fetchDrivers();
  },
  methods: {
    fetchDrivers() {
      axios.get('http://localhost:8083/api/drivers')
        .then(response => { this.drivers = response.data; })
        .catch(error => { console.error('Eroare la preluarea șoferilor:', error); });
    },
    openCreateDialog() {
      this.isEditMode = false;
      this.currentDriver = { 
        id: null,
        firstName: '', 
        lastName: '', 
        email: '', 
        phone: '', 
        licenseNumber: '', 
        hireDate: new Date().toISOString().slice(0, 10) 
      };
      this.dialog = true;
    },
    openEditDialog(driver) {
      this.isEditMode = true;
      this.currentDriver = { ...driver };
      this.dialog = true;
    },
    saveDriver() {
      if (this.isEditMode) {
        axios.put(`http://localhost:8083/api/drivers/${this.currentDriver.id}`, this.currentDriver)
          .then(() => {
            this.dialog = false;
            this.fetchDrivers();
          })
          .catch(error => console.error('Eroare la editare șofer:', error));
      } else {
        axios.post('http://localhost:8083/api/drivers', this.currentDriver)
          .then(() => {
            this.dialog = false;
            this.fetchDrivers();
          })
          .catch(error => console.error('Eroare la salvare șofer:', error));
      }
    },
    deleteDriver(id) {
      if (confirm('Sigur doriți să ștergeți acest șofer?')) {
        axios.delete(`http://localhost:8083/api/drivers/${id}`)
          .then(() => { this.fetchDrivers(); })
          .catch(err => console.error('Eroare la ștergere șofer:', err));
      }
    }
  }
}
</script>