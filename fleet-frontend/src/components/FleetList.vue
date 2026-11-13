<template>
  <v-container>
    <v-card class="pa-5 mt-5">
      <v-row align="center">
        <v-col cols="12" md="8">
          <v-card-title class="text-h4 px-0">Management Flote</v-card-title>
        </v-col>
        <v-col cols="12" md="4" class="text-right">
          <v-btn color="primary" @click="openCreateDialog">Adaugă Flotă Nouă</v-btn>
        </v-col>
      </v-row>
      
      <v-table class="mt-4">
        <thead>
          <tr>
            <th class="text-left">ID</th>
            <th class="text-left">Nume Flotă</th>
            <th class="text-left">Descriere</th>
            <th class="text-right">Acțiuni</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="fleet in fleets" :key="fleet.id">
            <td>{{ fleet.id }}</td>
            <td><strong>{{ fleet.name }}</strong></td>
            <td>{{ fleet.description }}</td>
            <td class="text-right">
              <v-btn icon size="small" color="blue" class="me-2" @click="openEditDialog(fleet)">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
              <v-btn icon size="small" color="red" @click="deleteFleet(fleet.id)">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </td>
          </tr>
          <tr v-if="fleets.length === 0">
            <td colspan="4" class="text-center text-grey">Nu există flote înregistrate.</td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- Dialog pentru Creare / Editare Flotă -->
    <v-dialog v-model="dialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ isEditMode ? 'Editează Flota' : 'Adaugă Flotă Nouă' }}</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-text-field v-model="currentFleet.name" label="Nume Flotă" required></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field v-model="currentFleet.description" label="Descriere"></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="dialog = false">Anulează</v-btn>
          <v-btn color="success" variant="text" @click="saveFleet">Salvează</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  name: 'FleetList',
  data() {
    return {
      fleets: [],
      dialog: false,
      isEditMode: false,
      currentFleet: {
        id: null,
        name: '',
        description: ''
      }
    }
  },
  mounted() {
    this.fetchFleets();
  },
  methods: {
    fetchFleets() {
      axios.get('http://localhost:8083/api/fleets')
        .then(response => { this.fleets = response.data; })
        .catch(error => { console.error('Eroare la preluarea flotelor:', error); });
    },
    openCreateDialog() {
      this.isEditMode = false;
      this.currentFleet = { id: null, name: '', description: '' };
      this.dialog = true;
    },
    openEditDialog(fleet) {
      this.isEditMode = true;
      this.currentFleet = { ...fleet }; // Copiem obiectul ca să nu modificăm direct în tabel înainte de salvare
      this.dialog = true;
    },
    saveFleet() {
      if (this.isEditMode) {
        // PUT / Update
        axios.put(`http://localhost:8083/api/fleets/${this.currentFleet.id}`, this.currentFleet)
          .then(() => {
            this.dialog = false;
            this.fetchFleets();
          })
          .catch(err => console.error(err));
      } else {
        // POST / Create
        axios.post('http://localhost:8083/api/fleets', this.currentFleet)
          .then(() => {
            this.dialog = false;
            this.fetchFleets();
          })
          .catch(err => console.error(err));
      }
    },
    deleteFleet(id) {
      if (confirm('Sigur vrei să ștergi această flotă?')) {
        axios.delete(`http://localhost:8083/api/fleets/${id}`)
          .then(() => {
            this.fetchFleets();
          })
          .catch(err => console.error("Eroare la ștergere:", err));
      }
    }
  }
}
</script>