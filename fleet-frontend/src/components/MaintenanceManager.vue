<template>
  <v-container>
    <v-card class="pa-5 mt-5">
      <v-row align="center">
        <v-col cols="12" md="8">
          <v-card-title class="text-h4 px-0">Jurnal Mentenanță și Service</v-card-title>
        </v-col>
        <v-col cols="12" md="4" class="text-right">
          <v-btn color="success" @click="openCreateDialog">Adaugă Înregistrare</v-btn>
        </v-col>
      </v-row>
      
      <v-table class="mt-4">
        <thead>
          <tr>
            <th class="text-left">ID</th>
            <th class="text-left">Vehicul (Nr. Înmatriculare)</th>
            <th class="text-left">Tip Service</th>
            <th class="text-left">Descriere</th>
            <th class="text-left">Cost Total (RON)</th>
            <th class="text-left">Data</th>
            <th class="text-right">Acțiuni</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="log in maintenanceLogs" :key="log.id">
            <td>{{ log.id }}</td>
            <td><strong>{{ log.vehicle ? log.vehicle.licensePlate : 'N/A' }}</strong></td>
            <td>
              <v-chip size="small" color="blue-lighten-3" class="text-black font-weight-bold" style="border: 1px solid rgba(0,0,0,0.2);">
                {{ log.serviceType }}
              </v-chip>
            </td>
            <td>{{ log.description }}</td>
            <td>{{ log.cost }} RON</td>
            <td>{{ log.logDate }}</td>
            <td class="text-right">
              <v-btn icon size="small" color="blue" class="me-2" @click="openEditDialog(log)">
                <v-icon>mdi-pencil</v-icon>
              </v-btn>
              <v-btn icon size="small" color="red" @click="deleteMaintenanceLog(log.id)">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </td>
          </tr>
          <tr v-if="maintenanceLogs.length === 0">
            <td colspan="7" class="text-center text-grey">Nu există înregistrări de mentenanță.</td>
          </tr>
        </tbody>
      </v-table>
    </v-card>

    <!-- Dialog Adăugare / Editare Mentenanță -->
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>
          <span class="text-h5">{{ isEditMode ? 'Editează Mentenanța' : 'Adaugă Înregistrare Nouă' }}</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12">
                <v-select
                  v-model="currentLog.vehicle"
                  :items="vehicles"
                  item-title="licensePlate"
                  item-value="id"
                  label="Selectează Vehiculul"
                  return-object
                  required
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="currentLog.serviceType" label="Tip Service (ex: Revizie, Reparație)" required></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model.number="currentLog.cost" label="Cost Total (RON)" type="number" required></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field v-model="currentLog.description" label="Descriere / Piese schimbate" required></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-text-field v-model="currentLog.logDate" label="Dată (YYYY-MM-DD)" required></v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey-darken-1" variant="text" @click="dialog = false">Anulează</v-btn>
          <v-btn color="success" variant="text" @click="saveMaintenanceLog">Salvează</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  name: 'MaintenanceManager',
  data() {
    return {
      maintenanceLogs: [],
      vehicles: [],
      dialog: false,
      isEditMode: false,
      currentLog: {
        id: null,
        vehicle: null,
        serviceType: '',
        cost: null,
        logDate: '',
        description: ''
      }
    }
  },
  mounted() {
    this.fetchMaintenanceLogs();
    this.fetchVehicles();
  },
  methods: {
    fetchMaintenanceLogs() {
      axios.get('http://localhost:8083/api/maintenance')
        .then(response => { this.maintenanceLogs = response.data; })
        .catch(error => { console.error('Eroare la preluarea mentenanței:', error); });
    },
    fetchVehicles() {
      axios.get('http://localhost:8083/api/vehicles')
        .then(response => { this.vehicles = response.data; })
        .catch(error => { console.error('Eroare la preluarea vehiculelor:', error); });
    },
    openCreateDialog() {
      this.isEditMode = false;
      this.currentLog = { 
        id: null,
        vehicle: null, 
        serviceType: '', 
        cost: null, 
        logDate: new Date().toISOString().slice(0, 10),
        description: '' 
      };
      this.dialog = true;
    },
    openEditDialog(log) {
      this.isEditMode = true;
      this.currentLog = { ...log };
      this.dialog = true;
    },
    saveMaintenanceLog() {
      if (this.isEditMode) {
        axios.put(`http://localhost:8083/api/maintenance/${this.currentLog.id}`, this.currentLog)
          .then(() => {
            this.dialog = false;
            this.fetchMaintenanceLogs();
          })
          .catch(error => console.error('Eroare la editare mentenanță:', error));
      } else {
        axios.post('http://localhost:8083/api/maintenance', this.currentLog)
          .then(() => {
            this.dialog = false;
            this.fetchMaintenanceLogs();
          })
          .catch(error => console.error('Eroare la salvare mentenanță:', error));
      }
    },
    deleteMaintenanceLog(id) {
      if (confirm('Sigur doriți să ștergeți această înregistrare de mentenanță?')) {
        axios.delete(`http://localhost:8083/api/maintenance/${id}`)
          .then(() => { this.fetchMaintenanceLogs(); })
          .catch(err => console.error('Eroare la ștergere mentenanță:', err));
      }
    }
  }
}
</script>