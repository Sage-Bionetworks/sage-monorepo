export const intersectFilterCallback = function (value: any, filters: any): boolean {
  if (filters === undefined || filters === null || filters.length < 1) {
    return true;
  } else if (value === undefined || value === null || filters.length < 1) {
    return false;
  }

  for (const filter of filters) {
    if (value.indexOf(filter) !== -1) {
      return true;
    }
  }

  return false;
};

export const urlLinkCallback = function (value: any, filters: any): boolean {
  console.log('urlLinkCallback', value, filters);
  if (filters === undefined || filters === null || filters.length < 1) {
    return true;
  } else if (value === undefined || value === null || filters.length < 1) {
    return false;
  }

  for (const filter of filters) {
    if (value.link_url && value.link_url.indexOf(filter) !== -1) {
      return true;
    }
  }

  return false;
};
