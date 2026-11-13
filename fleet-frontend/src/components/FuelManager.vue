<template>
  <v-container>
    <v-card class="pa-5 mt-5">
      <v-row align="center">
        <v-col cols="12" md="8">
          <v-card-title class="text-h4 px-0">Jurnal Alimentări Combustibil</v-card-title>
        </v-col>
        <v-col cols="12" md="4" class="text-right">
          <v-btn color="success" @click="openCreateDialog">Adaugă Alimentare</v-btn>
        </v-col>
      </v-row>
      
      <v-table class="mt-4">
        <thead>
          <tr>
            <th class="text-left">ID</th>
            <th class="text-left">Vehicul (Nr. Înmatriculare)</th>
            <th class="text-left">Tip Combustibil</th>
            <th class="text-left">Cantitate (Litri / kWh)</th>
            <th class="text-left">Cost Total (RON)</th>
            <th class="text-left">Data Alimentării</th>
            <th class="text-right">Acțiuni</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="record in fuelRecords" :key="record.id">
            <td>{{ record.id }}</td>
            <td><strong>{{ record.vehicle ? record.vehicle.licensePlate : 'N/A' }}</strong></td>
            <td>
              <v-chip
                :color="getFuelTypeColor(record.fuelType)"
                size="small"
                class="font-weight-bold text-black"
              >
                {{ record.fuelType }}
              </v-chip>
            </td>
            <td>{{ record.litersFilled }}</td>
            <td>{{ record.totalCost }} RON</td>
            <td>{{ record.fuelDate }}</td>
            <td class="text-right">
              <v-btn icon size="small" color="blue" class="me-2" @click="openEditDialog(record)">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
              <v-btn icon size="small" color="red" @click="deleteFuelRecord(record.id)">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </td>
          </tr>
          <tr v-if="fuelRecords.length === 0">
            <td colspan="7" class="text-center text-grey">Nu există alimentări înregistrate.</td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- Dialog Adăugare / Editare Alimentare -->
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ isEditMode ? 'Editează Alimentarea' : 'Adaugă Alimentare Nouă' }}</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-select
                  v-model="currentRecord.vehicle"
                  :items="vehicles"
                  item-title="licensePlate"
                  item-value="id"
                  label="Selectează Vehiculul"
                  return-object
                  required
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-select
                  v-model="currentRecord.fuelType"
                  :items="['Benzină', 'Motorină', 'Curent Electric', 'GPL']"
                  label="Tip Combustibil"
                  required
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model.number="currentRecord.litersFilled" label="Cantitate (Litri / kWh)" type="number" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model.number="currentRecord.totalCost" label="Cost Total (RON)" type="number" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentRecord.fuelDate" label="Dată (YYYY-MM-DD)" required></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="dialog = false">Anulează</v-btn>
          <v-btn color="success" variant="text" @click="saveFuelRecord">Salvează</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  name: 'FuelManager',
  data() {
    return {
      fuelRecords: [],
      vehicles: [],
      dialog: false,
      isEditMode: false,
      currentRecord: {
        id: null,
        vehicle: null,
        fuelType: 'Benzină',
        litersFilled: null,
        totalCost: null,
        fuelDate: ''
      }
    }
  },
  mounted() {
    this.fetchFuelRecords();
    this.fetchVehicles();
  },
  methods: {
    fetchFuelRecords() {
      axios.get('http://localhost:8083/api/fuel')
        .then(response => { this.fuelRecords = response.data; })
        .catch(error => { console.error('Eroare la preluarea alimentărilor:', error); });
    },
    fetchVehicles() {
      axios.get('http://localhost:8083/api/vehicles')
        .then(response => { this.vehicles = response.data; })
        .catch(error => { console.error('Eroare la preluarea vehiculelor:', error); });
    },
    openCreateDialog() {
      this.isEditMode = false;
      this.currentRecord = { 
        id: null,
        vehicle: null, 
        fuelType: 'Benzină', 
        litersFilled: null, 
        totalCost: null, 
        fuelDate: new Date().toISOString().slice(0, 10) 
      };
      this.dialog = true;
    },
    openEditDialog(record) {
      this.isEditMode = true;
      this.currentRecord = { ...record };
      this.dialog = true;
    },
    saveFuelRecord() {
      if (this.isEditMode) {
        axios.put(`http://localhost:8083/api/fuel/${this.currentRecord.id}`, this.currentRecord)
          .then(() => {
            this.dialog = false;
            this.fetchFuelRecords();
          })
          .catch(error => console.error('Eroare la editare alimentare:', error));
      } else {
        axios.post('http://localhost:8083/api/fuel', this.currentRecord)
          .then(() => {
            this.dialog = false;
            this.fetchFuelRecords();
          })
          .catch(error => console.error('Eroare la salvare alimentare:', error));
      }
    },
    deleteFuelRecord(id) {
      if (confirm('Sigur doriți să ștergeți această înregistrare?')) {
        axios.delete(`http://localhost:8083/api/fuel/${id}`)
          .then(() => { this.fetchFuelRecords(); })
          .catch(err => console.error('Eroare la ștergere alimentare:', err));
      }
    },
    getFuelTypeColor(type) {
      switch (type) {
        case 'Benzină': return 'blue-lighten-3';
        case 'Motorină': return 'amber-lighten-2';
        case 'Curent Electric': return 'green-lighten-3';
        case 'GPL': return 'purple-lighten-3';
        default: return 'grey-lighten-2';
      }
    }
  }
}
</script>