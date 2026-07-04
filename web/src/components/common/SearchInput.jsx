function SearchInput({ value, onChange, placeholder = "Search..." }) {
  return (
    <input
      className="dashboard-search"
      type="search"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      placeholder={placeholder}
    />
  );
}

export default SearchInput;
