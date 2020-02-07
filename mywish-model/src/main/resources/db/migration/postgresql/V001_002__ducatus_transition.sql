CREATE TABLE ducatus_transition (
  id SERIAL PRIMARY KEY ,
  address VARCHAR(66),
  amount NUMERIC (78, 0),
  tx_hash VARCHAR(66),
  transfer_status VARCHAR(20)
);