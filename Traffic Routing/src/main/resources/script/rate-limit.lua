local key = KEYS[1]

local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])

local now = redis.call('TIME')

local currentTime =
    now[1] + (now[2] / 1000000)

local bucket =
    redis.call('HMGET',
        key,
        'tokens',
        'lastRefill')

local tokens =
    tonumber(bucket[1])

local lastRefill =
    tonumber(bucket[2])

if not tokens then

    tokens = capacity

    lastRefill = currentTime

end

local elapsed =
    currentTime - lastRefill

local refill =
    elapsed * refillRate

tokens =
    math.min(
        capacity,
        tokens + refill)

if tokens < 1 then

    redis.call(
        'HMSET',
        key,
        'tokens', tokens,
        'lastRefill', currentTime)

    redis.call(
        'EXPIRE',
        key,
        60)

    return -1

end

tokens = tokens - 1

redis.call(
    'HMSET',
    key,
    'tokens', tokens,
    'lastRefill', currentTime)

redis.call(
    'EXPIRE',
    key,
    60)

return math.floor(tokens)