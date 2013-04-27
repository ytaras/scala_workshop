workflow issue {
  start state open
                 goes to started;
  state started  goes to open, resolved;
  state resolved goes to closed, open;
  state closed;
};

workflow project {
  start state negotiation
               goes to signed, failed;
  state signed goes to failed, done;
  state done   goes to paid, failed;
  state paid;
  state failed;
};
