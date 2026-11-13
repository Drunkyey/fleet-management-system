<template>
  <v-container>
    <v-card class="pa-5 mt-5">
      <v-row align="center">
        <v-col cols="12" md="8">
          <v-card-title class="text-h4 px-0">Jurnal Curse și Delegări</v-card-title>
        </v-col>
        <v-col cols="12" md="4" class="text-right">
          <v-btn color="success" @click="openCreateDialog">Adaugă Cursă</v-btn>
        </v-col>
      </v-row>
      
      <v-table class="mt-4">
        <thead>
          <tr>
            <th class="text-left">ID</th>
            <th class="text-left">Vehicul</th>
            <th class="text-left">Punct Plecare</th>
            <th class="text-left">Destinație</th>
            <th class="text-left">Distanță (Km)</th>
            <th class="text-left">Data Cursei</th>
            <th class="text-right">Acțiuni</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="trip in trips" :key="trip.id">
            <td>{{ trip.id }}</td>
            <td><strong>{{ trip.vehicle ? trip.vehicle.licensePlate : 'N/A' }}</strong></td>
            <td>
                <v-chip size="small" variant="flat" color="teal-lighten-4" style="color: black !important; font-weight: bold;">
                    {{ trip.startLocation }}
                </v-chip>
            </td>
            <td>
                <v-chip size="small" variant="flat" color="blue-grey-lighten-4" style="color: black !important; font-weight: bold;">
                    {{ trip.endLocation }}
                </v-chip>
            </td>
            <td>{{ trip.distanceKm }} Km</td>
            <td>{{ trip.tripDate }}</td>
            <td class="text-right">
              <v-btn icon size="small" color="blue" class="me-2" @click="openEditDialog(trip)">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
              <v-btn icon size="small" color="red" @click="deleteTrip(trip.id)">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </td>
          </tr>
          <tr v-if="trips.length === 0">
            <td colspan="7" class="text-center text-grey">Nu există curse înregistrate.</td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- Dialog Adăugare / Editare Cursă -->
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ isEditMode ? 'Editează Cursa' : 'Adaugă Cursă Nouă' }}</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-select
                  v-model="currentTrip.vehicle"
                  :items="vehicles"
                  item-title="licensePlate"
                  item-value="id"
                  label="Selectează Vehiculul"
                  return-object
                  required
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentTrip.startLocation" label="Punct de Plecare" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentTrip.endLocation" label="Destinație" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model.number="currentTrip.distanceKm" label="Distanță (Km)" type="number" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentTrip.tripDate" label="Data (YYYY-MM-DD)" required></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="dialog = false">Anulează</v-btn>
          <v-btn color="success" variant="text" @click="saveTrip">Salvează</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  name: 'TripManager',
  data() {
    return {
      trips: [],
      vehicles: [],
      dialog: false,
      isEditMode: false,
      currentTrip: {
        id: null,
        vehicle: null,
        startLocation: '',
        endLocation: '',
        distanceKm: null,
        tripDate: ''
      }
    }
  },
  mounted() {
    this.fetchTrips();
    this.fetchVehicles();
  },
  methods: {
    fetchTrips() {
      axios.get('http://localhost:8083/api/trips')
        .then(response => { this.trips = response.data; })
        .catch(error => { console.error('Eroare la preluarea curselor:', error); });
    },
    fetchVehicles() {
      axios.get('http://localhost:8083/api/vehicles')
        .then(response => { this.vehicles = response.data; })
        .catch(error => { console.error('Eroare la preluarea vehiculelor:', error); });
    },
    openCreateDialog() {
      this.isEditMode = false;
      this.currentTrip = { 
        id: null,
        vehicle: null, 
        startLocation: '', 
        endLocation: '', 
        distanceKm: null, 
        tripDate: new Date().toISOString().slice(0, 10) 
      };
      this.dialog = true;
    },
    openEditDialog(trip) {
      this.isEditMode = true;
      this.currentTrip = { ...trip };
      this.dialog = true;
    },
    saveTrip() {
      if (this.isEditMode) {
        axios.put(`http://localhost:8083/api/trips/${this.currentTrip.id}`, this.currentTrip)
          .then(() => {
            this.dialog = false;
            this.fetchTrips();
          })
          .catch(error => console.error('Eroare la editare cursă:', error));
      } else {
        axios.post('http://localhost:8083/api/trips', this.currentTrip)
          .then(() => {
            this.dialog = false;
            this.fetchTrips();
          })
          .catch(error => console.error('Eroare la salvare cursă:', error));
      }
    },
    deleteTrip(id) {
      if (confirm('Sigur doriți să ștergeți această cursă?')) {
        axios.delete(`http://localhost:8083/api/trips/${id}`)
          .then(() => { this.fetchTrips(); })
          .catch(err => console.error('Eroare la ștergere cursă:', err));
      }
    }
  }
}
</script>