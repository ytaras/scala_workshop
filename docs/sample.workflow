workflow issue {
  state open     goes to started;
  state started  goes to open, resolved;
  state resolved goes to closed, open;
  state closed;
}