package com.emna.micro_service4.dto;

public class FormulaDTO {

        private String id;
        private String formula;

        // Constructor
        public FormulaDTO(String id, String formula) {
            this.id = id;
            this.formula = formula;
        }

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFormula() {
            return formula;
        }

        public void setFormula(String formula) {
            this.formula = formula;
        }
    }

