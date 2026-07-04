import SearchInput from "./SearchInput";

function FilterBar({ search, onSearchChange, searchPlaceholder = "Search...", date, onDateChange, status, onStatusChange, statusOptions }) {
  return (
    <div className="dashboard-filters">
      {onSearchChange && (
        <SearchInput
          value={search}
          onChange={onSearchChange}
          placeholder={searchPlaceholder}
        />
      )}
      {onDateChange && (
        <input
          className="dashboard-search"
          type="date"
          value={date}
          onChange={(e) => onDateChange(e.target.value)}
        />
      )}
      {onStatusChange && statusOptions && (
        <select className="dashboard-select" value={status} onChange={(e) => onStatusChange(e.target.value)}>
          {statusOptions.map((option) => (
            <option key={option.value} value={option.value}>{option.label}</option>
          ))}
        </select>
      )}
    </div>
  );
}

export default FilterBar;
