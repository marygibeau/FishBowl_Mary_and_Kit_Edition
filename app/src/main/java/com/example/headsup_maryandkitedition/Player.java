package com.example.headsup_maryandkitedition;

public class Player {
        String name;
        int team;

        public Player (String name, int team) {
            this.name = name;
            this.team = team;
        }

        void setName(String name) {
            this.name = name;
        }

        String getName() {
            return this.name;
        }

        void setTeam(int i) {
            this.team = i;
        }

        int getTeam() {
            return this.team;
        }
    }
